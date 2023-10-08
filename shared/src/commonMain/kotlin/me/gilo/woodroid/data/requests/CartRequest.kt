package me.gilo.woodroid.data.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartRequest(
    @SerialName("id")
    var customerId: String? = null,

    @SerialName("thumb")
    var thumb: Boolean?
)