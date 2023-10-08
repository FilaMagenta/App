package com.arnyminerz.filamagenta.ui.state

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.network.Authorization.requestMe
import com.arnyminerz.filamagenta.network.httpClient
import com.arnyminerz.filamagenta.network.server.response.OAuthTokenResponse
import com.arnyminerz.filamagenta.ui.browser.BrowserManager
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.parameters
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

    /**
     * Uses [BrowserManager] to launch the url that requests the user to log in through the official website and
     * get a code, which can then be used with [requestToken].
     */
    fun launchLoginUrl() = scope.launch(Dispatchers.IO) {
        // First, request the server
        val url = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = BuildKonfig.ServerHostname,
            pathSegments = listOf("oauth", "authorize"),
            parameters = Parameters.build {
                set("client_id", BuildKonfig.OAuthClientId)
                set("redirect_uri", "app://filamagenta")
                set("response_type", "code")
            }
        ).buildString()

        BrowserManager.launchUrl(url)
    }

    /**
     * Once an authorization code has been obtained from [launchLoginUrl], it has to be passed to this function in
     * under 30 seconds.
     * This function then requests the server for a proper token and refresh token.
     */
    fun requestToken(code: String) = scope.launch(Dispatchers.IO) {
        if (_isRequestingToken.value) {
            println("Tried to request token while another request was in progress.")
            return@launch
        }
        _isRequestingToken.value = true

        val url = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = BuildKonfig.ServerHostname,
            pathSegments = listOf("oauth", "token")
        ).buildString()
        httpClient.submitForm(
            url = url,
            formParameters = parameters {
                append("grant_type", "authorization_code")
                append("code", code)
                append("client_id", BuildKonfig.OAuthClientId)
                append("client_secret", BuildKonfig.OAuthClientSecret)
                append("redirect_uri", "app://filamagenta")
            }
        ).apply {
            if (status == HttpStatusCode.OK) {
                val response: OAuthTokenResponse = body()

                // We now have an authorization code, request user's data
                requestMe(response)

                // accounts!!.addAccount()
            } else {
                println("Server responded with an error: ${bodyAsText()}")
            }
        }
    }.invokeOnCompletion { _isRequestingToken.value = false }
}
