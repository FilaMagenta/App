package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("LongParameterList")
class LineItem(
    val subtotal: String,
    @SerialName("subtotal_tax")
    val subtotalTax: String,
    val total: String,
    val totalTax: String? = null,
    val price: Double,
    val quantity: Int = 0,
    val taxClass: Int = 0,
    val name: String,

    @SerialName("product_id")
    val productId: Int = 0,

    val sku: String,
    val variations: String? = null,
    val meta: List<Metum> = ArrayList()
)
