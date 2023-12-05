package com.arnyminerz.filamagenta.ui.state

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.AccountData
import com.arnyminerz.filamagenta.account.Category
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.cache.data.qrcode
import com.arnyminerz.filamagenta.cache.data.toProductOrder
import com.arnyminerz.filamagenta.data.QrCodeScanResult
import com.arnyminerz.filamagenta.image.QRCodeGenerator
import com.arnyminerz.filamagenta.network.database.SqlServer
import com.arnyminerz.filamagenta.network.database.SqlServer.SelectParameter.InnerJoin
import com.arnyminerz.filamagenta.network.database.SqlServer.SelectParameter.Where
import com.arnyminerz.filamagenta.network.database.SqlTunnelException
import com.arnyminerz.filamagenta.network.database.getDate
import com.arnyminerz.filamagenta.network.database.getLong
import com.arnyminerz.filamagenta.network.database.getString
import com.arnyminerz.filamagenta.network.server.exception.WordpressException
import com.arnyminerz.filamagenta.network.woo.WooCommerce
import com.arnyminerz.filamagenta.network.woo.models.Order
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.getStringState
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.sync.EventsSyncHelper
import com.arnyminerz.filamagenta.sync.WalletSyncHelper
import com.arnyminerz.filamagenta.sync.utils.AccountUtils
import com.arnyminerz.filamagenta.ui.native.toImageBitmap
import io.github.aakira.napier.Napier
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.errors.IOException
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import kotlin.io.encoding.ExperimentalEncodingApi

@Suppress("TooManyFunctions")
class MainScreenModel : ScreenModel {

    private val _isLoadingWallet = MutableStateFlow(false)
    val isLoadingWallet: StateFlow<Boolean> get() = _isLoadingWallet

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> get() = _isLoadingEvents

    private val _isLoadingAccount = MutableStateFlow(false)
    val isLoadingAccount: StateFlow<Boolean> get() = _isLoadingAccount

    private val _isLoadingOrders = MutableStateFlow(false)
    val isLoadingOrders: StateFlow<Boolean> get() = _isLoadingOrders

    private val _isDownloadingTickets = MutableStateFlow(false)
    val isDownloadingTickets: StateFlow<Boolean> get() = _isDownloadingTickets

    private val _isUploadingScannedTickets = MutableStateFlow(false)
    val isUploadingScannedTickets: StateFlow<Boolean> get() = _isUploadingScannedTickets

    private val _editingField = MutableStateFlow<EventField<*>?>(null)
    val editingField: StateFlow<EventField<*>?> get() = _editingField

    private val _scanResult = MutableStateFlow<QrCodeScanResult?>(null)
    val scanResult: StateFlow<QrCodeScanResult?> get() = _scanResult

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> get() = _error


    private val _accountData = MutableStateFlow<AccountData?>(null)
    val accountData: StateFlow<AccountData?> get() = _accountData

    private val _profileQrCode = MutableStateFlow<ImageBitmap?>(null)
    val profileQrCode: StateFlow<ImageBitmap?> get() = _profileQrCode

    /**
     * Whether there's something being loaded in the background.
     */
    val isLoading = isLoadingWallet.combine(isLoadingEvents) { wallet, events ->
        wallet || events
    }

    /**
     * Stores the currently selected account.
     */
    val account = MutableStateFlow(
        settings.getStringOrNull(SettingsKeys.SELECTED_ACCOUNT).let { accountName ->
            val accountsList = accounts.getAccounts()

            if (accountName != null) {
                Napier.v("Selecting account $accountName")
                accountsList.find { it.name == accountName }
            } else {
                Napier.v("There isn't any account selected, choosing the first one...")
                accountsList.first()
            }
        }
    )

    /**
     * Stores whether the selected [account] is an admin.
     */
    val isAdmin = account.map { ac -> ac?.let(accounts::isAdmin) }

    /**
     * For each index of the pages in the main navigation, which function can be used for refreshing. If null, refresh
     * by [MainScreenModel] is not supported.
     */
    val refreshFunctions = listOf<(() -> Job)?>(
        // Wallet
        ::refreshWallet,
        // Events
        ::refreshEvents,
        // Account
        ::refreshAccount,
        // Settings
        null
    )

    /**
     * Stores the state flow that indicates when there's a process loading in the background for each page of the main
     * layout.
     */
    val isPageLoading = listOf(
        // Wallet
        isLoadingWallet,
        // Events
        isLoadingEvents,
        // Account
        isLoadingAccount,
        // Settings
        MutableStateFlow(false)
    )

    init {
        val accountsList = accounts.getAccounts()

        settings.addStringListener(SettingsKeys.SELECTED_ACCOUNT, "") { an ->
            CoroutineScope(Dispatchers.IO).launch {
                val accountName = an.takeIf { it.isNotBlank() }
                val newAccount = if (accountName != null) {
                    Napier.v("Selecting account $accountName")
                    accountsList.find { it.name == accountName }
                } else {
                    Napier.v("There isn't any account selected, choosing the first one...")
                    accountsList.first()
                }
                Napier.v("Selecting account ${newAccount?.name}")
                account.emit(newAccount)

                // Update the diagnostics information
                if (newAccount != null) {
                    getOrFetchAccountData() // make sure the data is available

                    val username = newAccount.name
                    val email = accounts.getEmail(newAccount)
                    Napier.v("Updating diagnostics information...")
                    Sentry.setUser(
                        User().apply {
                            this.username = username
                            this.email = email
                        }
                    )
                } else {
                    Napier.v("newAccount is null, removing diagnostics information...")
                    Sentry.setUser(null)
                }
            }
        }
    }

    /**
     * Tries getting the IdSocio from [accounts] for the selected [account].
     * If it's still not set, fetches it from the SQL server according to the user's [Account.name]
     *
     * @throws NullPointerException If [account] doesn't have a selected account.
     * @throws IllegalStateException If the server doesn't return a valid idSocio for [account].
     */
    private suspend fun getOrFetchIdSocio(): Int? {
        val account = account.value!!
        return try {
            AccountUtils.getOrFetchIdSocio(account)
        } catch (e: SqlTunnelException) {
            _error.emit(e)
            null
        } catch (e: SocketTimeoutException) {
            _error.emit(e)
            null
        }
    }

    /**
     * This method is used to get the data stored for an account.
     * If it is already stored in the database, it is retrieved from there.
     * Otherwise, a query is made to fetch all the required info from the "tbSocios" table and store it in the database
     * for future use.
     *
     * @return The data of the account.
     *
     * @throws IllegalStateException if data is null after fetching from the database.
     * @throws SqlTunnelException if there is an error in the SQLServer query.
     * @throws NullPointerException if [account]'s value is null
     */
    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun getOrFetchAccountData(): AccountData? {
        val account = account.value ?: return null
        val idSocio = getOrFetchIdSocio() ?: return null

        var data = try {
            accounts.getAccountData(account)
        } catch (_: MissingFieldException) {
            // If data is corrupted, or new fields have been added, ignore the stored one and fetch again
            null
        } catch (_: SerializationException) {
            // This can happen if some data has been changed, for example, an enum stored has new
            // elements, or something has been renamed
            null
        }
        data?.let { return it }

        try {
            Napier.i("Account doesn't have stored account data. Searching now...")
            val result = SqlServer.select(
                "tbSocios",
                "tbSocios.Nombre",
                "tbSocios.Apellidos",
                "tbSocios.Direccion",
                "tbCodPostales.CodPostal",
                "tbCodPostales.Poblacion",
                "tbSocios.FecNacimiento",
                "tbSocios.TlfParticular",
                "tbSocios.TlfMovil",
                "tbSocios.TlfTrabajo",
                "tbSocios.eMail",
                "tbSocios.idTipoFestero",
                InnerJoin(
                    sourceTable = "tbCodPostales",
                    modifyColumn = "tbSocios.idCodPostal",
                    sourceColumn = "tbCodPostales.idCodPostal"
                ),
                Where(
                    column = "idSocio",
                    value = idSocio
                )
            )
            Napier.v("Retrieved account data from SQL server. Processing result...")

            // we only have a query, so fetch that one
            Napier.v("Got ${result.size} resulting queries.")
            val entries = result[0]
            require(entries.isNotEmpty()) { "Could not find user in tbSocios." }

            // There should be one resulting entry, so take that one. We have already checked that there's one
            Napier.v("Got ${entries.size} rows in query. Retrieving columns...")
            val row = entries[0]

            val name = row.getString("Nombre")!!
            val surname = row.getString("Apellidos")!!
            val address = row.getString("Direccion")!!
            val postalCode = row.getLong("CodPostal")!!
            val city = row.getString("Poblacion")!!
                .replace("ALCOY", "ALCOI", ignoreCase = true)
            val birthday = row.getDate("FecNacimiento")!!
            val particularPhone = row.getString("TlfParticular")
            val mobilePhone = row.getString("TlfMovil")
            val workPhone = row.getString("TlfTrabajo")
            val email = row.getString("eMail")?.takeIf { it.isNotBlank() }
            val category = row.getLong("idTipoFestero")?.let(Category::forDatabaseId)
            data = AccountData(
                name,
                surname,
                address,
                postalCode,
                city,
                birthday,
                particularPhone,
                mobilePhone,
                workPhone,
                email,
                category
            )
            Napier.v("Account data ready, storing in accounts...")
            accounts.setAccountData(account, data)

            Napier.i("Updated account data for $account: $data")
        } catch (e: SqlTunnelException) {
            Napier.e("SQLServer returned an error.", throwable = e)
            return null
        } catch (e: Exception) {
            Napier.e("Could not request account data.", throwable = e)
            _error.emit(e)
            return null
        }

        return data
    }

    /**
     * Fetches all the transactions from the SQL server, and updates the local cache.
     */
    fun refreshWallet() = screenModelScope.launch(Dispatchers.IO) {
        try {
            _isLoadingWallet.emit(true)

            val idSocio = getOrFetchIdSocio() ?: return@launch

            WalletSyncHelper.synchronize(idSocio)
        } catch (e: IOException) {
            _error.emit(e)
        } finally {
            _isLoadingWallet.emit(false)
        }
    }

    /**
     * Fetches all events from the server and updates the local cache.
     */
    fun refreshEvents() = screenModelScope.launch(Dispatchers.IO) {
        try {
            _isLoadingEvents.emit(true)

            EventsSyncHelper.synchronize()
        } catch (e: WordpressException) {
            _error.emit(e)
        } catch (_: CancellationException) {
            // Ignore errors with cancellations
        } finally {
            _isLoadingEvents.emit(false)
        }
    }

    fun fetchOrders(productId: Int) = screenModelScope.launch(Dispatchers.IO) {
        // Wait until another thread finishes loading
        while (_isLoadingOrders.value) {
            delay(1)
        }

        try {
            _isLoadingOrders.emit(true)

            val customerId = getOrFetchCustomerId()

            Napier.d("Fetching orders for Product#$productId made by Customer#$customerId")
            val orders = WooCommerce.Orders.getOrdersForProductAndCustomer(customerId, productId)
            Napier.i("Got ${orders.size} orders for Product#$productId and Customer#$customerId. Updating cache...")

            orders.flatMap(Order::toProductOrder)
                .forEach { order ->
                    Napier.d("Inserting Order#${order.id}")
                    Cache.insertOrUpdate(order)
                }
        } catch (_: CancellationException) {
            Napier.w("The orders fetching for Product#$productId was cancelled.")
        } finally {
            _isLoadingOrders.emit(false)
        }
    }

    fun dismissScanResult() {
        _scanResult.value = null
    }

    /*
    /**
     * Validates the data contained in a QR for event assistance.
     */
    fun validateQr(source: String) = screenModelScope.launch(Dispatchers.IO) {
        QRCodeValidator.validateQRCode(source, _scanResult, viewingEvent.value)
    }

    fun processNfcTag(data: String) = screenModelScope.launch(Dispatchers.IO) {
        _scanResult.emit(QrCodeScanResult.Loading)

        val viewingEventId = settings.getLongOrNull(SettingsKeys.SYS_VIEWING_EVENT)
        if (viewingEventId != null) {
            val event = database.eventQueries
                .getById(viewingEventId)
                .executeAsOneOrNull() ?: return@launch
            viewEvent(event)

            // wait until the event is selected at most for 5 seconds
            withTimeout(5_000) {
                while (viewingEvent.value == null) {
                    delay(1)
                }
            }
        }

        validateQr(data)
    }*/

    /**
     * Dismisses the current value of [error], if any.
     */
    fun dismissError() = screenModelScope.launch { _error.emit(null) }

    /**
     * Requests de cache or SQL server for the current user's account data, and updates [accountData] accordingly.
     */
    fun refreshAccount() = screenModelScope.launch(Dispatchers.IO) {
        val data = getOrFetchAccountData()
        _accountData.emit(data)
    }

    /**
     * Loads the QR code for the current [account], and stores it into [profileQrCode].
     *
     * @return A [Job] that can be observed to know when the load has been completed
     */
    @ExperimentalEncodingApi
    @ExperimentalUnsignedTypes
    fun loadProfileQRCode() = screenModelScope.launch(Dispatchers.IO) {
        // Make sure the fields have been loaded before calling qrcode
        getOrFetchIdSocio()
        getOrFetchCustomerId()

        val qr = account.value!!.qrcode()
        val data = qr.encrypt()
        Napier.d("Profile QR: $data")
        val image = QRCodeGenerator.generate(data).toImageBitmap()
        Napier.d("Generated QR code image.")
        _profileQrCode.emit(image)
    }
}
