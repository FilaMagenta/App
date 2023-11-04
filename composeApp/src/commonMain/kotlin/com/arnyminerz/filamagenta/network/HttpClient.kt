package com.arnyminerz.filamagenta.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json

val httpClient = HttpClient {
    install(HttpCookies)
    install(ContentNegotiation) {
        json()
    }
}
