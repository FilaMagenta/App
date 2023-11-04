package com.arnyminerz.filamagenta.network

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.network.server.exception.ServerException
import com.arnyminerz.filamagenta.network.server.request.OAuthRefreshTokenRequest
import com.arnyminerz.filamagenta.network.server.response.OAuthMeResponse
import com.arnyminerz.filamagenta.network.server.response.OAuthTokenResponse
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.parameters

object Authorization {
    private const val HTTP_OK_MIN = 200
    private const val HTTP_OK_MAX = 299

    /**
     * Makes sure the response is successful by checking its status code and making sure it's between `200`
     * ([HTTP_OK_MIN]) and `299` ([HTTP_OK_MAX]) both inclusively.
     * Otherwise, throws [ServerException].
     */
    private suspend fun HttpResponse.assertSuccess() {
        if (status.value < HTTP_OK_MIN || status.value >= HTTP_OK_MAX) {
            throw ServerException(status, bodyAsText())
        }
    }

    /**
     * Sends a request to the server to obtain an access token after being authorized by the user.
     *
     * @param code The code returned by the server after the user confirms its identity.
     */
    suspend fun requestToken(code: String): OAuthTokenResponse {
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
            assertSuccess()

            return body<OAuthTokenResponse>()
        }
    }

    /**
     * Requests the server for the logged-in user's data using the `oauth/me` endpoint.
     *
     * **Note: It's considered that the [token] has just been obtained, and it has not expired.
     * No refresh will be made.**
     */
    suspend fun requestMe(token: OAuthTokenResponse): OAuthMeResponse {
        val url = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = BuildKonfig.ServerHostname,
            pathSegments = listOf("oauth", "me")
        ).build()
        httpClient.get(
            url = url
        ) {
            bearerAuth(token.accessToken)
        }.apply {
            assertSuccess()

            return body<OAuthMeResponse>()
        }
    }

    suspend fun refreshToken(refreshToken: String): OAuthTokenResponse {
        val url = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = BuildKonfig.ServerHostname,
            pathSegments = listOf("oauth", "token")
        ).build()
        httpClient.post(
            url
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                OAuthRefreshTokenRequest(refreshToken = refreshToken)
            )
        }.apply {
            assertSuccess()

            return body<OAuthTokenResponse>()
        }
    }
}
