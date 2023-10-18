package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.ProductOrder
import com.arnyminerz.filamagenta.network.woo.models.Metadata
import com.arnyminerz.filamagenta.network.woo.models.Order
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
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

const val OrderQRIndexOrderId = 0
const val OrderQRIndexEventId = 1
const val OrderQRIndexOrderNumber = 2
const val OrderQRIndexCustomerId = 3
const val OrderQRIndexCustomerName = 4
const val OrderQRFieldsCount = 5

@ExperimentalEncodingApi
fun ProductOrder.qrcode(): String {
    val text = "$id/$eventId/$orderNumber/$customerId/$customerName"
    return Base64.encode(text.encodeToByteArray())
}

fun validateProductQr(data: String): Boolean {
    val pieces = data.split('/')
    return pieces.size == OrderQRFieldsCount
}
