package me.gilo.woodroid.repo

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.builtin.CallConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

open class WooRepository(baseUrl: String, consumerKey: String, consumerSecret: String) {

    var ktorfit: Ktorfit

    init {
        val client = HttpClient {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    }
                )
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = consumerKey,
                            password = consumerSecret
                        )
                    }
                }
            }
        }

        ktorfit = Ktorfit.Builder()
            .baseUrl(baseUrl)
            .converterFactories(CallConverterFactory())
            .httpClient(client)
            .build()
    }
}
