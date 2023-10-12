package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
data class CouponLine(
    val id: Int,
    val code: String,
    val discount: String,
    val discount_tax: String,
    val meta_data: List<com.arnyminerz.filamagenta.network.woo.models.Metadata>
)
