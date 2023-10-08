package com.arnyminerz.filamagenta.network

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.network.server.exception.ServerException
import com.arnyminerz.filamagenta.network.server.request.OAuthRefreshTokenRequest
import com.arnyminerz.filamagenta.network.server.response.OAuthTokenResponse
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.contentType

object Authorization {
    private const val HTTP_OK_MIN = 200
    private const val HTTP_OK_MAX = 299

    /**
     * Requests the server for the logged-in user's data using the `oauth/me` endpoint.
     *
     * **Note: It's considered that the [token] has just been obtained, and it has not expired.
     * No refresh will be made.**
     */
    suspend fun requestMe(token: OAuthTokenResponse) {
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
            if (status.value < HTTP_OK_MIN || status.value >= HTTP_OK_MAX) {
                throw ServerException()
            }

            val body = bodyAsText()
            println("Me body: $body")
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
        }.let { response ->
            val status = response.status
            if (status.value < HTTP_OK_MIN || status.value >= HTTP_OK_MAX) {
                throw ServerException()
            }
            return response.body<OAuthTokenResponse>()
        }
    }
}
