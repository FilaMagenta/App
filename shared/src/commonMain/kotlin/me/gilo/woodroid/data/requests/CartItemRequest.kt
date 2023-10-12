package me.gilo.woodroid.data.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.gilo.woodroid.models.Variation

@Serializable
data class CartItemRequest(
    @SerialName("product_id")
    var productId: Int,

    @SerialName("quantity")
    var quantity: Int,

    @SerialName("variation_id")
    var variationId: Int? = null,

    @SerialName("variation")
    var variation: Variation? = null,

    @SerialName("cart_item_data")
    var cartItemData: com.arnyminerz.filamagenta.network.woo.models.CartItem? = null,

    @SerialName("refresh_totals")
    var refreshTotals: Boolean? = null,

    @SerialName("return_cart")
    var returnCart: Boolean? = null
)