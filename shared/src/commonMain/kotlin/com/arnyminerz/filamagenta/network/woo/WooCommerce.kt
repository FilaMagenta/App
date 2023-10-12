package com.arnyminerz.filamagenta.network.woo

import com.arnyminerz.filamagenta.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

object WooCommerce {
    private val baseUrl = "https://${BuildKonfig.ServerHostname}/wp-json/wc/v3/"

    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(BuildKonfig.WooClientId, BuildKonfig.WooClientSecret)
                }
            }
        }
    }

}
