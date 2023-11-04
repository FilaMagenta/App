package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.ProductOrder
import com.arnyminerz.filamagenta.network.woo.models.Metadata
import com.arnyminerz.filamagenta.network.woo.models.Order
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.encodeToString

/**
 * Converts all the items ordered in the current [Order] into [ProductOrder].
 */
fun Order.toProductOrder(): List<ProductOrder> = lineItems.map {
    ProductOrder(
        id.toLong(),
        dateModified.toInstant(TimeZone.UTC).toEpochMilliseconds(),
        it.productId.toLong(),
        orderNumber,
        dateCreated,
        customerId.toLong(),
        "${billingAddress.firstName} ${billingAddress.lastName}",
        DefaultJson.encodeToString(metadata)
    )
}

/**
 * Converts the cached raw metadata into a proper list.
 */
val ProductOrder.metadata: List<Metadata> get() = DefaultJson.decodeFromString(_cache_meta_data)

/**
 * Checks whether the order has been validated or not.
 */
val ProductOrder.hasBeenValidated: Boolean get() = metadata.find { it.key == "validated" }?.value == "true"
