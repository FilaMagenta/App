package com.arnyminerz.filamagenta.ui.state

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.OrderQRIndexCustomerId
import com.arnyminerz.filamagenta.cache.data.OrderQRIndexCustomerName
import com.arnyminerz.filamagenta.cache.data.OrderQRIndexEventId
import com.arnyminerz.filamagenta.cache.data.OrderQRIndexOrderId
import com.arnyminerz.filamagenta.cache.data.OrderQRIndexOrderNumber
import com.arnyminerz.filamagenta.cache.data.extractMetadata
import com.arnyminerz.filamagenta.cache.data.toAccountTransaction
import com.arnyminerz.filamagenta.cache.data.toEvent
import com.arnyminerz.filamagenta.cache.data.toProductOrder
import com.arnyminerz.filamagenta.cache.data.validateProductQr
import com.arnyminerz.filamagenta.cache.database
import com.arnyminerz.filamagenta.data.QrCodeScanResult
import com.arnyminerz.filamagenta.diagnostics.performance.Performance
import com.arnyminerz.filamagenta.diagnostics.performance.TransactionStatus
import com.arnyminerz.filamagenta.network.Authorization
import com.arnyminerz.filamagenta.network.database.SqlServer
import com.arnyminerz.filamagenta.network.database.SqlTunnelEntry
import com.arnyminerz.filamagenta.network.database.SqlTunnelException
import com.arnyminerz.filamagenta.network.database.getLong
import com.arnyminerz.filamagenta.network.database.getString
import com.arnyminerz.filamagenta.network.httpClient
import com.arnyminerz.filamagenta.network.oauth.LoginData
import com.arnyminerz.filamagenta.network.server.exception.WordpressException
import com.arnyminerz.filamagenta.network.woo.WooCommerce
import com.arnyminerz.filamagenta.network.woo.models.Metadata
import com.arnyminerz.filamagenta.network.woo.models.Order
import com.arnyminerz.filamagenta.network.woo.update.BatchMetadataUpdate
import com.arnyminerz.filamagenta.network.woo.update.MetadataUpdate
import com.arnyminerz.filamagenta.network.woo.utils.ProductMeta
import com.arnyminerz.filamagenta.network.woo.utils.set
import com.arnyminerz.filamagenta.utils.toEpochMillisecondsString
import com.doublesymmetry.viewmodel.ViewModel
import io.github.aakira.napier.Napier
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.utils.io.errors.IOException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Suppress("TooManyFunctions")
class MainViewModel : ViewModel() {
    companion object {
        private const val MONTH_INDEX_AUGUST = 8
    }

    private val _isRequestingToken = MutableStateFlow(false)

    /** Reports the progress of [requestToken]. */
    val isRequestingToken: StateFlow<Boolean> get() = _isRequestingToken

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

    private val _viewingEvent = MutableStateFlow<Event?>(null)
    val viewingEvent: StateFlow<Event?> get() = _viewingEvent

    private val _editingField = MutableStateFlow<EventField<*>?>(null)
    val editingField: StateFlow<EventField<*>?> get() = _editingField

    private val _scanningQr = MutableStateFlow(false)
    val scanningQr: StateFlow<Boolean> get() = _scanningQr

    private val _scanResult = MutableStateFlow<QrCodeScanResult?>(null)
    val scanResult: StateFlow<QrCodeScanResult?> get() = _scanResult

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> get() = _error


    private val _accountFullName = MutableStateFlow<String?>(null)
    val accountFullName: StateFlow<String?> get() = _accountFullName

    /**
     * Whether there's something being loaded in the background.
     */
    val isLoading = isLoadingWallet.combine(isLoadingEvents) { wallet, events ->
        wallet || events
    }

    /**
     * Stores the currently selected account.
     */
    val account = MutableStateFlow<Account?>(null)

    /**
     * Stores whether the selected [account] is an admin.
     */
    val isAdmin = account.map { ac -> ac?.let(accounts::isAdmin) }

    /**
     * For each index of the pages in the main navigation, which function can be used for refreshing. If null, refresh
     * by [MainViewModel] is not supported.
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

    val loginError = MutableStateFlow(false)

    /**
     * Provides the login form target URL.
     */
    private val loginUrl: String
        get() = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = BuildKonfig.ServerHostname,
            pathSegments = listOf("wp-login.php")
        ).buildString()

    private fun getAuthorizeUrl() =
        // First, request the server
        URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = BuildKonfig.ServerHostname,
            pathSegments = listOf("oauth", "authorize"),
            parameters = Parameters.build {
                set("client_id", BuildKonfig.OAuthClientId)
                set("redirect_uri", "app://filamagenta")
                set("response_type", "code")
            }
        ).buildString()

    fun login(username: String, password: String): Job = viewModelScope.launch(Dispatchers.IO) {
        val loginData = LoginData(
            BuildKonfig.OAuthClientId,
            username,
            password,
            getAuthorizeUrl()
        )

        loginError.emit(false)

        Napier.d("Trying to log in with credentials... Username=$username")

        val next = httpClient.submitForm(
            url = loginUrl,
            formParameters = parameters {
                loginData.append(this)
            }
        ).let { it.headers[HttpHeaders.Location] }

        if (next == null) {
            Napier.e("Login credentials are not correct.")

            loginError.emit(true)
            return@launch
        }

        val codeLocation = httpClient.get(next).let { it.headers[HttpHeaders.Location] }
        if (codeLocation?.startsWith("app://filamagenta") == true) {
            // Redirection complete, extract code
            val query = Url(codeLocation)
                .encodedQuery
                .split("&")
                .associate { it.split("=").let { (k, v) -> k to v } }
            val code = query["code"]
            if (code == null) {
                _error.emit(
                    RuntimeException("Authentication was redirected without a valid code.")
                )
                return@launch
            }

            requestToken(code)
        } else {
            Napier.e("Authorization failed.")

            loginError.emit(true)
            return@launch
        }
    }

    /**
     * Requests the server for a token and refresh token, then adds the account to the account manager.
     */
    fun requestToken(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_isRequestingToken.value) {
                println("Tried to request token while another request was in progress.")
                return@launch
            }
            _isRequestingToken.emit(true)

            val token = Authorization.requestToken(code)
            val me = Authorization.requestMe(token)

            val account = Account(me.userLogin)
            accounts.addAccount(account, token.toAccessToken(), me.userRoles.contains("administrator"), me.userEmail)
        }.invokeOnCompletion { _isRequestingToken.value = false }
    }

    /**
     * Gets the start date of the current working year.
     * This can be used for fetching events only for the desired date range.
     *
     * @return The beginning date of the current working year.
     * Will always be the 1st of August, the thing that changes is the year.
     */
    fun getWorkingYearStart(): LocalDate {
        // Events will only be fetched for this year. Year is considered until August
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = if (now.monthNumber > MONTH_INDEX_AUGUST) {
            // If right now is after August, the working year is the current one
            now.year
        } else {
            // If before August, working year is the last one
            now.year - 1
        }
        return LocalDate(year, Month.AUGUST, 1)
    }

    /**
     * Requests the UI to display the given event.
     *
     * Use [stopViewingEvent] to stop displaying.
     */
    fun viewEvent(event: Event) = viewModelScope.launch { _viewingEvent.emit(event) }

    /**
     * Requests the UI to stop displaying the selected event, if any ([viewingEvent]).
     *
     * Use [viewEvent] to start displaying an event.
     */
    fun stopViewingEvent() = viewModelScope.launch {

        _viewingEvent.emit(null)
        _editingField.emit(null)
    }

    /**
     * Starts editing the given [field] for the currently selected event ([viewingEvent]).
     * Does nothing if [viewingEvent] is null.
     */
    fun edit(field: EventField<*>) {
        if (_viewingEvent.value == null) return
        viewModelScope.launch { _editingField.emit(field) }
    }

    /**
     * Clears the value of [editingField].
     */
    fun cancelEdit() {
        viewModelScope.launch { _editingField.emit(null) }
    }

    /**
     * Updates [scanningQr] to `true`.
     */
    fun startScanner() {
        _scanningQr.value = true
    }

    /**
     * Updates [scanningQr] to `false`.
     */
    fun stopScanner() {
        _scanningQr.value = false
    }

    /**
     * Requests the server to store the update made at [field].
     */
    fun <T> performUpdate(event: Event, field: EventField<T>): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val rawValue = field.value
            val (key, value) = when (field) {
                is EventField.Name -> throw UnsupportedOperationException("Cannot change names right now")
                is EventField.Date -> ProductMeta.EVENT_DATE to (rawValue as LocalDateTime).toEpochMillisecondsString()
                is EventField.Type -> ProductMeta.CATEGORY to (rawValue as EventType).name
            }

            val product = WooCommerce.Products.update(
                event.id,
                MetadataUpdate(
                    event.extractMetadata().set(key, value)
                )
            )
            // fixme - should include variations
            val newEvent = product.toEvent(emptyList())

            Cache.insertOrUpdate(newEvent)
            viewEvent(newEvent)
        }
    }

    /**
     * Tries getting the IdSocio from [accounts] for the selected [account].
     * If it's still not set, fetches it from the SQL server according to the user's [Account.name]
     *
     * @throws NullPointerException If [account] doesn't have a selected account.
     * @throws IllegalStateException If the server doesn't return a valid idSocio for [account].
     */
    private suspend fun getOrFetchIdSocio(): Int {
        val account = account.value!!
        var idSocio = accounts.getIdSocio(account)
        if (idSocio == null) {
            try {
                Napier.i("Account doesn't have an stored idSocio. Searching now...")
                val result = SqlServer.query("SELECT idSocio FROM tbSocios WHERE Dni='${account.name}';")
                check(result.isNotEmpty()) { "SQLServer returned a null or empty list." }

                // we only have a query, so fetch that one
                val entries = result[0]
                require(entries.isNotEmpty()) { "Could not find user in tbSocios." }

                // There should be one resulting entry, so take that one. We have already checked that there's one
                val row = entries[0]

                idSocio = row.getLong("idSocio")!!.toInt()
                accounts.setIdSocio(account, idSocio)

                Napier.i("Updated idSocio for $account: $idSocio")
            } catch (e: SqlTunnelException) {
                Napier.e("SQLServer returned an error.", throwable = e)
            }
        }
        checkNotNull(idSocio) { "idSocio must not be null." }

        return idSocio
    }

    /**
     * This method is used to get the full name of an account.
     * If the full name is already stored in the database, it is retrieved from there.
     * Otherwise, a query is made to fetch the full name from the "tbSocios" table and store it in the database for
     * future use.
     *
     * @return The full name of the account.
     *
     * @throws IllegalStateException if the full name is null after fetching from the database.
     * @throws SqlTunnelException if there is an error in the SQLServer query.
     */
    private suspend fun getOrFetchFullName(): String {
        val account = account.value!!
        val idSocio = getOrFetchIdSocio()
        var fullName = accounts.getFullName(account)
        if (fullName == null) {
            try {
                Napier.i("Account doesn't have an stored full name. Searching now...")
                val result = SqlServer.query("SELECT Nombre, Apellidos FROM tbSocios WHERE idSocio=$idSocio;")

                // we only have a query, so fetch that one
                val entries = result[0]
                require(entries.isNotEmpty()) { "Could not find user in tbSocios." }

                // There should be one resulting entry, so take that one. We have already checked that there's one
                val row = entries[0]

                val name = row.getString("Nombre")
                val surname = row.getString("Apellidos")
                fullName = "$name $surname"
                accounts.setFullName(account, fullName)

                Napier.i("Updated full name for $account: $fullName")
            } catch (e: SqlTunnelException) {
                Napier.e("SQLServer returned an error.", throwable = e)
            }
        }
        checkNotNull(fullName) { "fullName must not be null." }

        return fullName
    }

    /**
     * Fetches all the transactions from the SQL server, and updates the local cache.
     */
    fun refreshWallet() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _isLoadingWallet.emit(true)

            val idSocio = getOrFetchIdSocio()

            Napier.d("Getting transactions list from server...")
            val result = SqlServer.query("SELECT * FROM tbApuntesSocios WHERE idSocio=$idSocio;")[0]
            Cache.synchronizeTransactions(
                result.map(List<SqlTunnelEntry>::toAccountTransaction)
            )
        } catch (e: IOException) {
            _error.emit(e)
        } finally {
            _isLoadingWallet.emit(false)
        }
    }

    /**
     * Returns the stored WooCommerce's Customer ID for the selected [account] if there's one stored, or searches in the
     * server for one if there's none stored.
     *
     * @throws NullPointerException If [account] doesn't have a selected account.
     * @throws IllegalStateException If the server doesn't return a valid customer id for [account].
     */
    private suspend fun getOrFetchCustomerId(): Int {
        val account = account.value!!
        var customerId = accounts.getCustomerId(account)

        if (customerId == null) {
            try {
                Napier.i("Account doesn't have an stored customerId. Searching now...")

                val customer = WooCommerce.Customers.search(account.name)
                checkNotNull(customer) { "A user that matches \"${account.name}\" was not found in the server." }

                customerId = customer.id
                accounts.setCustomerId(account, customerId)
                Napier.i("Updated customerId for $account: $customerId")
            } catch (e: SqlTunnelException) {
                Napier.e("SQLServer returned an error.", throwable = e)
            }
        }

        checkNotNull(customerId) { "customerId must not be null." }

        return customerId
    }

    /**
     * Fetches all events from the server and updates the local cache.
     */
    fun refreshEvents() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _isLoadingEvents.emit(true)

            // Events will only be fetched for this year. Year is considered until August
            val modifiedAfter = getWorkingYearStart()

            Napier.d("Getting products from server after $modifiedAfter...")

            WooCommerce.Products.getProductsAndVariations(modifiedAfter).also { pairs ->
                Napier.i("Got ${pairs.size} products from server. Updating cache...")
                Cache.synchronizeEvents(
                    pairs.map { (product, variations) -> product.toEvent(variations) }
                )
            }
        } catch (e: WordpressException) {
            _error.emit(e)
        } finally {
            _isLoadingEvents.emit(false)
        }
    }

    fun fetchOrders(productId: Int) = viewModelScope.launch(Dispatchers.IO) {
        // Wait until another thread finishes loading
        while (_isLoadingOrders.value) {
            delay(1)
        }

        try {
            _isLoadingOrders.emit(true)

            val customerId = getOrFetchCustomerId()

            Napier.d("Fetching orders for Product#$productId made by Customer#$customerId")
            val orders = WooCommerce.Orders.getOrdersForProductAndCustomer(customerId, productId)
            Napier.i("Got ${orders.size} for Product#$productId. Updating cache...")

            orders.flatMap(Order::toProductOrder)
                .forEach { order ->
                    Napier.d("Inserting Order#${order.id}")
                    Cache.insertOrUpdate(order)
                }
        } finally {
            _isLoadingOrders.emit(false)
        }
    }

    fun dismissScanResult() {
        _scanResult.value = null
    }

    /**
     * Validates the data contained in a QR for event assistance.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun validateQr(data: String) = viewModelScope.launch(Dispatchers.IO) {
        val decoded = Base64.decode(data).decodeToString()
        if (!validateProductQr(decoded)) {
            Napier.i("Got invalid QR")
            _scanResult.emit(QrCodeScanResult.Invalid)
            return@launch
        }

        val split = decoded.split("/")
        val eventId = split[OrderQRIndexEventId].toLong()
        val orderId = split[OrderQRIndexOrderId].toLong()
        val orderNumber = split[OrderQRIndexOrderNumber]
        val customerId = split[OrderQRIndexCustomerId].toLong()
        val customerName = split[OrderQRIndexCustomerName]

        Napier.i("Got valid QR, checking if stored...")
        val ticket = database.adminTicketsQueries.getById(orderId).executeAsOneOrNull()
        if (ticket == null) {
            Napier.i("QR is not stored locally")
            _scanResult.emit(QrCodeScanResult.Invalid)
            return@launch
        }

        Napier.i("QR is stored, checking if reused...")

        if (ticket.isValidated) {
            _scanResult.emit(QrCodeScanResult.AlreadyUsed)
        } else {
            Cache.updateIsValidated(orderId, true)

            _scanResult.emit(QrCodeScanResult.Success(customerName, orderNumber))
        }
    }

    fun downloadTickets(eventId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val transaction = Performance.measure("MainViewModel", "downloadTickets()")
        try {
            _isDownloadingTickets.emit(true)

            val orders = WooCommerce.Orders.getOrdersForProduct(eventId.toInt())
            for (order: Order in orders) {
                order.toProductOrder().forEach(Cache::insertOrUpdateAdminTicket)
            }
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            transaction.setThrowable(e)
            transaction.setStatus(TransactionStatus.INTERNAL_ERROR)
            throw e
        } finally {
            _isDownloadingTickets.emit(false)
            transaction.finish()
        }
    }

    fun deleteTickets(eventId: Long) = viewModelScope.launch(Dispatchers.IO) {
        database.adminTicketsQueries.deleteByEventId(eventId)
    }

    fun syncScannedTickets(eventId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _isUploadingScannedTickets.emit(true)

            val update = mutableListOf<BatchMetadataUpdate.Entry>()

            val tickets = database.adminTicketsQueries.getByEventId(eventId).executeAsList()
            for (ticket in tickets) {
                // First get the order associated with the event
                val order = database.productOrderQueries.getById(ticket.orderId).executeAsOneOrNull() ?: continue
                // Now compare the update time of the order with the one stored at the ticket, they should match
                if (order.lastUpdate < ticket.lastUpdate) {
                    // If the ticket was updated after the order, add for the update
                    val existingMeta = DefaultJson.decodeFromString<List<Metadata>>(ticket._cache_meta_data)
                        .set("validated", "true")

                    update.add(
                        BatchMetadataUpdate.Entry(
                            ticket.orderId,
                            existingMeta
                        )
                    )
                }
            }

            WooCommerce.Orders.batchUpdateMetadata(
                BatchMetadataUpdate(update)
            )
        } finally {
            _isUploadingScannedTickets.emit(false)
        }
    }

    /**
     * Dismisses the current value of [error], if any.
     */
    fun dismissError() = viewModelScope.launch { _error.emit(null) }

    fun refreshAccount() = viewModelScope.launch(Dispatchers.IO) {
        val name = getOrFetchFullName()
        _accountFullName.emit(name)
    }
}
