package com.arnyminerz.filamagenta.network.server.response

import com.arnyminerz.filamagenta.account.AccessToken
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("token_type") val tokenType: String,
    @SerialName("scope") val scope: String,
    @SerialName("refresh_token") val refreshToken: String
) {
    private val timestamp: Instant = Clock.System.now()

    val expiration: Instant = timestamp.plus(expiresIn, DateTimeUnit.SECOND)

    /**
     * Converts the response into an [AccessToken].
     */
    fun toAccessToken(): AccessToken = AccessToken(accessToken, timestamp, expiresIn, refreshToken)
}
