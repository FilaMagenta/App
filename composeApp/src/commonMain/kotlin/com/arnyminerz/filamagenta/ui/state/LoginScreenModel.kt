package com.arnyminerz.filamagenta.ui.state

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.network.Authorization
import com.arnyminerz.filamagenta.network.httpClient
import com.arnyminerz.filamagenta.network.oauth.LoginData
import io.github.aakira.napier.Napier
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginScreenModel: ScreenModel {
    private val _isRequestingToken = MutableStateFlow(false)

    /** Reports the progress of [requestToken]. */
    val isRequestingToken: StateFlow<Boolean> get() = _isRequestingToken

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> get() = _error

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

    fun login(username: String, password: String): Job = screenModelScope.launch(Dispatchers.IO) {
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
            Napier.e("Authorization failed. Location: $codeLocation. Next: $next")

            loginError.emit(true)
            return@launch
        }
    }

    /**
     * Requests the server for a token and refresh token, then adds the account to the account manager.
     */
    private fun requestToken(code: String) = screenModelScope.launch(Dispatchers.IO) {
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
