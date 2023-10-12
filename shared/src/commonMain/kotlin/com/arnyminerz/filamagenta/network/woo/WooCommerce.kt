package com.arnyminerz.filamagenta.network.woo

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.network.woo.models.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object WooCommerce {
    private val baseUrl = "https://${BuildKonfig.ServerHostname}/wp-json/wc/v3/"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(BuildKonfig.WooClientId, BuildKonfig.WooClientSecret)
                }
            }
        }
    }

    private suspend fun get(
        vararg pathSegments: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse {
        return client.get(
            URLBuilder(baseUrl)
                .appendPathSegments(*pathSegments)
                .build(),
            block
        )
    }

    object Products {
        suspend fun getProducts(): List<Product> {
            val request = get("products")
            return request.body<List<Product>>()
        }
    }
}
