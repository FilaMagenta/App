package com.arnyminerz.filamagenta.network.woo

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.network.woo.models.Product
import com.arnyminerz.filamagenta.network.woo.update.MetadataUpdate
import com.arnyminerz.filamagenta.network.woo.update.WooProductUpdate
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
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
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(message, null, "WooCommerce")
                }
            }
            level = LogLevel.ALL
        }
    }

    private suspend fun get(
        vararg pathSegments: Any,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse {
        return client.get(
            URLBuilder(baseUrl)
                .appendPathSegments(pathSegments.map { it.toString() })
                .build(),
            block
        )
    }

    private suspend fun <T: Any> getList(
        type: TypeInfo,
        vararg pathSegments: Any,
        page: Int = 1,
        perPage: Int = 10,
        block: HttpRequestBuilder.() -> Unit = {}
    ): List<T> {
        val builder = mutableListOf<T>()

        Napier.v("Fetching page $page[$perPage] for ${pathSegments.joinToString("/")}")

        val baseUrl = URLBuilder(baseUrl)
            .appendPathSegments(pathSegments.map { it.toString() })
            .build()
        client.get(
            URLBuilder(
                protocol = baseUrl.protocol,
                host = baseUrl.host,
                port = baseUrl.port,
                pathSegments = baseUrl.pathSegments,
                parameters = parameters {
                    set("per_page", perPage.toString())
                    set("page", page.toString())
                }
            ).build(),
            block
        ).apply {
            if (status == HttpStatusCode.OK) {
                val pages = headers["X-WP-TotalPages"]?.toInt() ?: 1
                if (page < pages) {
                    builder.addAll(
                        getList(type, *pathSegments, page = page + 1, perPage = perPage, block = block)
                    )
                }
                builder.addAll(
                    body<List<T>>(type)
                )
            } else {
                Napier.e("Listing for ${pathSegments.joinToString("/")} failed with ($status): ${bodyAsText()}")
            }
        }

        return builder
    }

    private suspend inline fun <reified T: Any> getList(
        vararg pathSegments: Any,
        page: Int = 1,
        perPage: Int = 10,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): List<T> = getList(typeInfo<List<T>>(), *pathSegments, page = page, perPage = perPage, block = block)

    private suspend fun put(
        vararg pathSegments: Any,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse {
        return client.post(
            URLBuilder(baseUrl)
                .appendPathSegments(pathSegments.map { it.toString() })
                .build()
        ) {
            header("X-HTTP-Method-Override", "PUT")
            block()
        }
    }

    object Products {
        suspend fun getProducts(): List<Product> {
            return getList("products", perPage = 50)
        }

        /**
         * Performs an update to the product with id [productId].
         * 
         * [API reference](https://woocommerce.github.io/woocommerce-rest-api-docs/#update-a-product)
         * 
         * @param productId The ID ([Product.id] of the product to update.
         * @param update The update to make. Must be a serializable object of type [WooProductUpdate].
         *
         * @return The updated product returned by the server.
         */
        suspend fun update(productId: Long, update: WooProductUpdate): Product {
            val request = put("products", productId) {
                contentType(ContentType.Application.Json)
                when (update) {
                    is MetadataUpdate -> setBody(update)
                }
            }
            return request.body()
        }
    }
}
