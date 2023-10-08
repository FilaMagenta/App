package me.gilo.woodroid.models

import kotlinx.serialization.Serializable

/**
 * Created by gilo on 2/18/16.
 */
@Serializable
class CartItem {
    lateinit var product: Product
    lateinit var options: ArrayList<Option>
    var qty: Int = 0
}
