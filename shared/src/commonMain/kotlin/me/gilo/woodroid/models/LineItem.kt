package me.gilo.woodroid.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LineItem {

    lateinit var subtotal: String
    @SerialName("subtotal_tax")
    lateinit var subtotalTax: String
    lateinit var total: String
    lateinit var totalTax: String
    lateinit var price: String
    var quantity: Int = 0
    var taxClass: Int = 0
    lateinit var name: String

    @SerialName("product_id")
    var productId: Int = 0

    lateinit var sku: String
    lateinit var variations: String
    var meta: List<Metum> = ArrayList()
}
