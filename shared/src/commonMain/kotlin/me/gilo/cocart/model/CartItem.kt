package me.gilo.cocart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.gilo.woodroid.models.TaxClass
import me.gilo.woodroid.models.Variation

@Serializable
class CartItem {

    @SerialName("product_id")
    var productId: Int = 0

    @SerialName("variation_id")
    var variationId: Int? = null

    var variation: Array<Variation>? = null

    lateinit var subtotal: String
    @SerialName("subtotal_tax")
    lateinit var subtotalTax: String
    lateinit var total: String
    lateinit var totalTax: String
    lateinit var price: String
    var quantity: Int = 0
    lateinit var taxClass: TaxClass
    lateinit var name: String


    lateinit var key: String
    lateinit var sku: String
    lateinit var variations: String
    lateinit var data_hash: String
}
