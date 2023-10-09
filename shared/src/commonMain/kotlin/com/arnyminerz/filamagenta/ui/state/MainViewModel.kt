package com.arnyminerz.filamagenta.ui.state

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
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
}
