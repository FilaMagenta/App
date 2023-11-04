package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class Category {
    var id: Int = 0
    var name: String? = null
    var slug: String? = null
    var parent: Int = 0
    var description: String? = null
    var display: String? = null
    var image: com.arnyminerz.filamagenta.network.woo.models.Image? = null
    var menu_order: Int = 0
    var count: Int = 0
}
