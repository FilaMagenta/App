package com.arnyminerz.filamagenta.ui.state

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.extractMetadata
import com.arnyminerz.filamagenta.cache.data.toEvent
import com.arnyminerz.filamagenta.network.Authorization
import com.arnyminerz.filamagenta.network.woo.WooCommerce
import com.arnyminerz.filamagenta.network.woo.update.MetadataUpdate
import com.arnyminerz.filamagenta.network.woo.utils.ProductMeta
import com.arnyminerz.filamagenta.network.woo.utils.set
import com.arnyminerz.filamagenta.utils.toEpochMillisecondsString
import com.doublesymmetry.viewmodel.ViewModel
import io.github.aakira.napier.Napier
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MainViewModel : ViewModel() {
    companion object {
        private const val MONTH_INDEX_AUGUST = 8
    }

    private val _isRequestingToken = MutableStateFlow(false)

    /**
     * Reports the progress of [requestToken].
     */
    val isRequestingToken: StateFlow<Boolean> get() = _isRequestingToken

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> get() = _isLoadingEvents

    private val _viewingEvent = MutableStateFlow<Event?>(null)
    val viewingEvent: StateFlow<Event?> get() = _viewingEvent

    private val _editingField = MutableStateFlow<EventField<*>?>(null)
    val editingField: StateFlow<EventField<*>?> get() = _editingField

    fun getAuthorizeUrl() =
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
            accounts.addAccount(account, token.toAccessToken(), me.userRoles.contains("administrator"))
        }.invokeOnCompletion { _isRequestingToken.value = false }
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
     * Requests the server to store the update made at [field].
     */
    fun <T> performUpdate(event: Event, field: EventField<T>): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val rawValue = field.value
            val (key, value) = when(field) {
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
     * Fetches all events from the server and updates the local cache.
     */
    fun refreshEvents() = viewModelScope.launch(Dispatchers.IO) {
        _isLoadingEvents.emit(true)

        // Events will only be fetched for this year. Year is considered until August
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = if (now.monthNumber > MONTH_INDEX_AUGUST) {
            // If right now is after August, the working year is the current one
            now.year
        } else {
            // If before August, working year is the last one
            now.year - 1
        }
        val modifiedAfter = LocalDate(year, Month.AUGUST, 1)

        Napier.d("Getting products from server after $modifiedAfter...")

        WooCommerce.Products.getProductsAndVariations(modifiedAfter).also { pairs ->
            Napier.i("Got ${pairs.size} products from server. Updating cache...")
            Cache.synchronizeEvents(
                pairs.map { (product, variations) -> product.toEvent(variations) }
            )
        }

        _isLoadingEvents.emit(false)
    }
}
