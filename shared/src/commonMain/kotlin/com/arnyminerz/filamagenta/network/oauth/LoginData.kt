package com.arnyminerz.filamagenta.network.oauth

import io.ktor.http.ParametersBuilder
import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    val clientId: String,
    val username: String,
    val password: String,
    val redirectTo: String,
    val testCookie: Int = 1
) {
    fun append(builder: ParametersBuilder) {
        builder.append("client_id", clientId)
        builder.append("log", username)
        builder.append("pwd", password)
        builder.append("redirect_to", redirectTo)
        builder.append("testcookie", testCookie.toString())
    }
}
