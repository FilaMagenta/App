package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class ProductAttribute {
    var id: Int = 0
    var name: String? = null
    var slug: String? = null
    var type: String? = null
    val option: String? = null
    var position: Int = 0
    var isVisible: Boolean = false
    var isVariation: Boolean = false
    var options: Array<String>? = null
}
