package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class DefaultAttribute {
    var id: Int = 0
    lateinit var name: String
    lateinit var option: String
}
