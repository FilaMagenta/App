package me.gilo.woodroid.models

import kotlinx.serialization.Serializable

@Serializable
class ShippingZone {
    var id: Int = 0
    var name: String? = null
    var order: Int = 0
}
