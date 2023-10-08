package me.gilo.woodroid.models

import kotlinx.serialization.Serializable

@Serializable
class TopSellers {
    var title: String? = null
    var product_id: Int = 0
    var quantity: Int = 0
}
