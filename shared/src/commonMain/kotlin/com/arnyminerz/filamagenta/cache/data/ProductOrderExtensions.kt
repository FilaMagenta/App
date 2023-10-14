package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.ProductOrder
import com.arnyminerz.filamagenta.network.woo.models.Order
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.encodeToString

/**
 * Converts all the items ordered in the current [Order] into [ProductOrder].
 */
fun Order.toProductOrder(): List<ProductOrder> = lineItems.map {
    ProductOrder(
        id.toLong(),
        it.productId.toLong(),
        orderNumber,
        dateCreated,
        customerId.toLong(),
        "${billingAddress.firstName} ${billingAddress.lastName}",
        DefaultJson.encodeToString(metadata)
    )
}

const val OrderQRIndexOrderId = 0
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
