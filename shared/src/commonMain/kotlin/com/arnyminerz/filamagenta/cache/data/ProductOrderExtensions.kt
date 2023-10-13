package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.ProductOrder
import com.arnyminerz.filamagenta.network.woo.models.Order

/**
 * Converts all the items ordered in the current [Order] into [ProductOrder].
 */
fun Order.toProductOrder(): List<ProductOrder> = lineItems.map {
    ProductOrder(id.toLong(), it.productId.toLong(), orderNumber, dateCreated, "${billingAddress.firstName} ${billingAddress.lastName}")
}
