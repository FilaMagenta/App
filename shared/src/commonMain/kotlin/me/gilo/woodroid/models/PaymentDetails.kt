package me.gilo.woodroid.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PaymentDetails {
    @SerialName("method_id")
    lateinit var methodId: String
    @SerialName("method_title")
    lateinit var methodTitle: String
    var paid: Boolean? = null
}
