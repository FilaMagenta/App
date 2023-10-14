package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.ProductOrder
import com.arnyminerz.filamagenta.network.woo.models.Order
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Converts all the items ordered in the current [Order] into [ProductOrder].
 */
fun Order.toProductOrder(): List<ProductOrder> = lineItems.map {
    ProductOrder(id.toLong(), it.productId.toLong(), orderNumber, dateCreated, "${billingAddress.firstName} ${billingAddress.lastName}")
}

@ExperimentalEncodingApi
fun ProductOrder.qrcode(): String {
    val text = "$id/$eventId/$orderNumber/${customerName.replace(" ", "").lowercase()}"
    return Base64.encode(text.encodeToByteArray())
}
