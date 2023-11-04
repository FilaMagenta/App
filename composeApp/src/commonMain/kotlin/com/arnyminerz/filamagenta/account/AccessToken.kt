package com.arnyminerz.filamagenta.account

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus

/**
 * Stores the data returned by the authorization endpoint of the server, which has the user's
 * authentication token, when the token was expedited, and when it expires, as well as the refresh
 * token for fetching a new one when needed.
 */
data class AccessToken(
    val token: String,
    val timestamp: Instant,
    val expiresIn: Long,
    val refreshToken: String
) {
    /**
     * The moment when [token] expires, and it's required that a new token is obtained using the
     * [refreshToken].
     */
    val expiration: Instant = timestamp.plus(expiresIn, DateTimeUnit.SECOND)
}
