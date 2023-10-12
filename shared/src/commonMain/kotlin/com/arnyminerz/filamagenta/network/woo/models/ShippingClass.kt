package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class ShippingClass {
    var id: Int = 0
    lateinit var name: String
    lateinit var slug: String
    lateinit var description: String
    var count: Int = 0
}
