package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("LongParameterList")
class Customer(
    val id: Int = 0,

    @SerialName("created_at")
    val createdAt: String? = null,

    val email: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String,

    val username: String,
    val password: String? = null,
    val role: String,

    @SerialName("last_order_id")
    val lastOrderId: String? = null,

    @SerialName("last_order_date")
    val lastOrderDate: String? = null,

    @SerialName("orders_count")
    val ordersCount: Int = 0,

    @SerialName("total_spent")
    val totalSpent: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String,

    @SerialName("billing")
    val billingAddress: BillingAddress,

    @SerialName("shipping")
    val shippingAddress: ShippingAddress,
)
