package me.gilo.woodroid.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Order {
    var id: Int = 0
    @SerialName("number")
    lateinit var orderNumber: String
    @SerialName("created_at")
    lateinit var createdAt: String

    @SerialName("date_created")
    lateinit var dateCreated: LocalDateTime

    @SerialName("updated_at")
    lateinit var updatedAt: String
    @SerialName("completed_at")
    lateinit var completedAt: String
    lateinit var status: String
    lateinit var currency: String
    lateinit var total: String
    lateinit var subtotal: String
    @SerialName("total_line_items_quantity")
    var totalLineItemsQuantity: Int = 0
    @SerialName("total_tax")
    lateinit var totalTax: String
    @SerialName("total_shipping")
    lateinit var totalShipping: String
    @SerialName("cart_tax")
    lateinit var cartTax: String
    @SerialName("shipping_tax")
    lateinit var shippingTax: String
    @SerialName("total_discount")
    lateinit var totalDiscount: String
    @SerialName("shipping_methods")
    lateinit var shippingMethods: String
    @SerialName("payment_details")
    lateinit var paymentDetails: PaymentDetails
    @SerialName("billing")
    lateinit var billingAddress: BillingAddress
    @SerialName("shipping")
    lateinit var shippingAddress: ShippingAddress
    lateinit var note: String
    @SerialName("customer_ip")
    lateinit var customerIp: String
    @SerialName("customer_user_agent")
    lateinit var customerUserAgent: String
    @SerialName("customer_id")
    var customerId: Int? = null
    @SerialName("view_order_url")
    lateinit var viewOrderUrl: String
    @SerialName("line_items")
    var lineItems: MutableList<LineItem> = ArrayList()
    @SerialName("shipping_lines")
    var shippingLines: List<ShippingLine> = ArrayList()
    @SerialName("tax_lines")
    var taxLines: List<TaxLine> = ArrayList()
    @SerialName("fee_lines")
    var feeLines: List<FeeLine> = ArrayList()
    @SerialName("coupon_lines")
    var couponLines: List<CouponLine> = ArrayList()
    lateinit var customer: Customer


    fun addLineItem(lineItem: LineItem) {
        lineItems.add(lineItem)

    }
}
