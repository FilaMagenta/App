package me.gilo.cocart.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartTotal (

    @SerialName("subtotal")
    var subtotal: String? = "",

    @SerialName("subtotal_tax")
    var subtotalTax: Float? = 0f,

    @SerialName("shipping_total")
    var shippingTotal: String? = "",

    @SerialName("shipping_tax")
    var shippingTax: Float? = 0f,

    @SerialName("shipping_taxes")
    var shippingTaxes: Map<String, Float>? = HashMap(),

    @SerialName("discount_total")
    var discountTotal: Float? = 0f,

    @SerialName("discount_tax")
    var discountTax: Float? = 0f,

    @SerialName("cart_contents_total")
    var cartContentsTotal: String? = "",

    @SerialName("cart_contents_tax")
    var cartContentsTax:  Float? = 0f,

    @SerialName("cart_contents_taxes")
    var cartContentsTaxes: Map<String, Float>? = HashMap(),

    @SerialName("fee_total")
    var feeTotal: String? = "",

    @SerialName("fee_tax")
    var feeTax: String? = "",

    @SerialName("fee_taxes")
    var feeTaxes: String,

    @SerialName("total")
    var total: String? = "",

    @SerialName("total_tax")
    var totalTax:  Float? = 0f


    )
