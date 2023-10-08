package me.gilo.woodroid.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ShippingLine {
    @SerialName("method_id")
    var id: String? = null
    @SerialName("method_title")
    var methodTitle: String? = null
    var total: Int = 0
}
