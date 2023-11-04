package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

/**
 * Created by gilo on 2/18/16.
 */
@Serializable
class CartItem {
    lateinit var product: com.arnyminerz.filamagenta.network.woo.models.Product
    lateinit var options: ArrayList<com.arnyminerz.filamagenta.network.woo.models.Option>
    var qty: Int = 0
}
