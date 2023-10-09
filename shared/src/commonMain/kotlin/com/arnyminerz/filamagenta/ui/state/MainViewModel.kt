package com.arnyminerz.filamagenta.ui.state

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.network.Authorization
import com.doublesymmetry.viewmodel.ViewModel
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isRequestingToken = MutableStateFlow(false)

    /**
     * Reports the progress of [requestToken].
     */
    val isRequestingToken: StateFlow<Boolean> get() = _isRequestingToken

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
            accounts!!.addAccount(account, token.toAccessToken(), me.userRoles.contains("administrator"))
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
}
