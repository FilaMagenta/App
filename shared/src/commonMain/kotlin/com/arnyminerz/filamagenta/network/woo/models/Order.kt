package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Suppress("LongParameterList")
class Order(
    val id: Int = 0,
    @SerialName("number")
    val orderNumber: String,
    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("date_created")
    val dateCreated: LocalDateTime,

    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("completed_at")
    val completedAt: String? = null,
    val status: String,
    val currency: String,
    val total: String,
    val subtotal: String? = null,
    @SerialName("total_line_items_quantity")
    val totalLineItemsQuantity: Int = 0,
    @SerialName("total_tax")
    val totalTax: String,
    @SerialName("total_shipping")
    val totalShipping: String? = null,
    @SerialName("cart_tax")
    val cartTax: String,
    @SerialName("shipping_tax")
    val shippingTax: String,
    @SerialName("total_discount")
    val totalDiscount: String? = null,
    @SerialName("shipping_methods")
    val shippingMethods: String? = null,
    @SerialName("payment_details")
    val paymentDetails: PaymentDetails? = null,
    @SerialName("billing")
    val billingAddress: BillingAddress,
    @SerialName("shipping")
    val shippingAddress: ShippingAddress,
    val note: String? = null,
    @SerialName("customer_ip")
    val customerIp: String? = null,
    @SerialName("customer_user_agent")
    val customerUserAgent: String,
    @SerialName("customer_id")
    val customerId: Int? = null,
    @SerialName("view_order_url")
    val viewOrderUrl: String? = null,
    @SerialName("line_items")
    val lineItems: MutableList<LineItem> = ArrayList(),
    @SerialName("shipping_lines")
    val shippingLines: List<ShippingLine> = ArrayList(),
    @SerialName("tax_lines")
    val taxLines: List<TaxLine> = ArrayList(),
    @SerialName("fee_lines")
    val feeLines: List<FeeLine> = ArrayList(),
    @SerialName("coupon_lines")
    val couponLines: List<CouponLine> = ArrayList(),
    val customer: Customer? = null
)
