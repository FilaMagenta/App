package me.gilo.woodroid.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Customer {
    var id: Int = 0

    @SerialName("created_at")
    lateinit var createdAt: String

    lateinit var email: String

    @SerialName("first_name")
    lateinit var firstName: String

    @SerialName("last_name")
    lateinit var lastName: String

    lateinit var username: String
    lateinit var password: String
    lateinit var role: String

    @SerialName("last_order_id")
    lateinit var lastOrderId: String

    @SerialName("last_order_date")
    lateinit var lastOrderDate: String

    @SerialName("orders_count")
    var ordersCount: Int = 0

    @SerialName("total_spent")
    lateinit var totalSpent: String

    @SerialName("avatar_url")
    lateinit var avatarUrl: String

    @SerialName("billing")
    lateinit var billingAddress: BillingAddress

    @SerialName("shipping")
    lateinit var shippingAddress: ShippingAddress


}
