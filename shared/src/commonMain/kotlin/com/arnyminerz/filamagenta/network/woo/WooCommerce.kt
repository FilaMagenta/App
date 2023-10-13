package com.arnyminerz.filamagenta.network.woo

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.network.woo.models.Customer
import com.arnyminerz.filamagenta.network.woo.models.Order
import com.arnyminerz.filamagenta.network.woo.models.Product
import com.arnyminerz.filamagenta.network.woo.models.Variation
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
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

    private suspend fun <T : Any> getList(
        type: TypeInfo,
        vararg pathSegments: Any,
        parameters: Map<String, Any?> = emptyMap(),
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
                    for ((k, v) in parameters) {
                        if (v != null) {
                            set(k, v.toString())
                        }
                    }

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

    private suspend inline fun <reified T : Any> getList(
        vararg pathSegments: Any,
        parameters: Map<String, Any?> = emptyMap(),
        page: Int = 1,
        perPage: Int = 10,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): List<T> = getList(
        type = typeInfo<List<T>>(),
        *pathSegments,
        parameters = parameters,
        page = page,
        perPage = perPage,
        block = block
    )

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
        private const val CATEGORY_ID_EVENTS = 21

        private val productsJob = SupervisorJob()
        private val coroutineScope = CoroutineScope(Dispatchers.IO + productsJob)

        /**
         * Gets all the products available in the server in the events category ([CATEGORY_ID_EVENTS]).
         */
        suspend fun getProducts(modifiedAfter: LocalDate? = null): List<Product> {
            return getList(
                "products",
                perPage = 50,
                parameters = mapOf(
                    "category" to CATEGORY_ID_EVENTS,
                    "modified_after" to modifiedAfter?.toString() + "T00:00:00"
                )
            )
        }

        suspend fun getProductsAndVariations(modifiedAfter: LocalDate? = null): Map<Product, List<Variation>> {
            val products = getProducts(modifiedAfter)
            val variationsCache = mutableMapOf<Int, Variation>()
            val result = mutableMapOf<Product, List<Variation>>()

            val jobs = mutableListOf<Job>()

            for (product in products) {
                if (product.variations.isEmpty()) {
                    Napier.d("Product#${product.id} doesn't have any variations.")
                    jobs += coroutineScope.launch {
                        result[product] = emptyList()
                    }
                } else {
                    Napier.d("Product#${product.id} has ${product.variations.size} variations.")
                    jobs += coroutineScope.launch {
                        if (product.variations.all { variationsCache.containsKey(it) }) {
                            // All the variations are cached, load from there
                            Napier.v("Variations for Product#${product.id} are available in cache.")
                            val variations = variationsCache.filterKeys { product.variations.contains(it) }
                            result[product] = variations.values.toList()
                        } else {
                            // Variations are missing from cache, load from server
                            Napier.v("Fetching variations for Product#${product.id} from server.")
                            val variations = getList<Variation>("products", product.id, "variations")
                            result[product] = variations
                            // Store all the variations in memory
                            variationsCache.putAll(
                                variations.associateBy { it.id }
                            )
                        }
                    }
                }
            }
            jobs.forEachIndexed { index, job ->
                job.join()

                Napier.d("Progress: $index / ${products.size}")
            }

            return result
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

    object Customers {
        /**
         * Searches for a customer that matches the given [query].
         *
         * @param query The string to search for. Preferably the user's login username.
         *
         * @return The user requested, or `null` if none was found.
         */
        suspend fun search(query: String): Customer? {
            val customers = getList<Customer>(
                "customers",
                parameters = mapOf(
                    "search" to query,
                    "role" to "all"
                )
            )
            return customers.firstOrNull()
        }
    }

    object Orders {
        /**
         * Fetches all the orders made by the customer with id [customerId], for the product with id [productId].
         */
        suspend fun getOrdersForProductAndCustomer(customerId: Int, productId: Int): List<Order> {
            return getList<Order>(
                "orders",
                parameters = mapOf("customer" to customerId, "product" to productId)
            )
        }
    }
}
