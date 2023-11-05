package com.arnyminerz.filamagenta.ui.state

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.AccountData
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.extractMetadata
import com.arnyminerz.filamagenta.cache.data.toAccountTransaction
import com.arnyminerz.filamagenta.cache.data.toEvent
import com.arnyminerz.filamagenta.cache.data.toProductOrder
import com.arnyminerz.filamagenta.cache.database
import com.arnyminerz.filamagenta.data.QrCodeScanResult
import com.arnyminerz.filamagenta.image.QRCodeValidator
import com.arnyminerz.filamagenta.network.Authorization
import com.arnyminerz.filamagenta.network.database.SqlServer
import com.arnyminerz.filamagenta.network.database.SqlServer.SelectParameter.InnerJoin
import com.arnyminerz.filamagenta.network.database.SqlServer.SelectParameter.Where
import com.arnyminerz.filamagenta.network.database.SqlTunnelEntry
import com.arnyminerz.filamagenta.network.database.SqlTunnelException
import com.arnyminerz.filamagenta.network.database.getDate
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
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.utils.toEpochMillisecondsString
import com.doublesymmetry.viewmodel.ViewModel
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.utils.io.CancellationException
import io.ktor.utils.io.errors.IOException
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.protocol.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException

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


    private val _accountData = MutableStateFlow<AccountData?>(null)
    val accountData: StateFlow<AccountData?> get() = _accountData

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
    private fun requestToken(code: String) = viewModelScope.launch(Dispatchers.IO) {
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

    fun updateSelectedAccount() = viewModelScope.launch {
        val accountsList = accounts.getAccounts()

        if (accountsList.isNotEmpty()) {
            Napier.v("Checking if there's already a selected account...")
            val accountName = settings.getStringOrNull(SettingsKeys.SELECTED_ACCOUNT)
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

    /**
     * Gets the start date of the current working year.
     * This can be used for fetching events only for the desired date range.
     *
     * @return The beginning date of the current working year.
     * Will always be the 1st of August, the thing that changes is the year.
     */
    private fun getWorkingYearStart(): LocalDate {
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
    fun viewEvent(event: Event) = viewModelScope.launch {
        Napier.d("Viewing event ${event.id}")
        _viewingEvent.emit(event)

        settings[SettingsKeys.SYS_VIEWING_EVENT] = event.id
    }

    /**
     * Requests the UI to stop displaying the selected event, if any ([viewingEvent]).
     *
     * Use [viewEvent] to start displaying an event.
     */
    fun stopViewingEvent() = viewModelScope.launch {
        Napier.d("Stopped viewing event")
        settings.remove(SettingsKeys.SYS_VIEWING_EVENT)

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
    suspend fun getOrFetchIdSocio(): Int? {
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
                _error.emit(e)
                return null
            } catch (e: SocketTimeoutException) {
                Napier.e("Connection timed out while trying to fetch idSocio from server.")
                _error.emit(e)
                return null
            }
        }
        checkNotNull(idSocio) { "idSocio must not be null." }

        return idSocio
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
                email
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
    fun refreshWallet() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _isLoadingWallet.emit(true)

            val idSocio = getOrFetchIdSocio() ?: return@launch

            Napier.d("Getting transactions list from server...")
            val result = SqlServer.select("tbApuntesSocios", "*", Where("idSocio", idSocio))[0]
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
    suspend fun getOrFetchCustomerId(): Int {
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

    /**
     * Validates the data contained in a QR for event assistance.
     */
    fun validateQr(source: String) = viewModelScope.launch(Dispatchers.IO) {
        QRCodeValidator.validateQRCode(source, _scanResult, viewingEvent.value)
    }

    fun processNfcTag(data: String) = viewModelScope.launch(Dispatchers.IO) {
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
    }

    fun downloadTickets(eventId: Long) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _isDownloadingTickets.emit(true)

            val orders = WooCommerce.Orders.getOrdersForProduct(eventId.toInt())
            for (order: Order in orders) {
                order.toProductOrder().forEach(Cache::insertOrUpdateAdminTicket)
            }
        } finally {
            _isDownloadingTickets.emit(false)
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

    /**
     * Requests de cache or SQL server for the current user's account data, and updates [accountData] accordingly.
     */
    fun refreshAccount() = viewModelScope.launch(Dispatchers.IO) {
        val data = getOrFetchAccountData()
        _accountData.emit(data)
    }
}
