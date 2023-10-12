package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class AttributeTerm {
    var id: Int = 0
    var name: String? = null
    var slug: String? = null
    lateinit var description: String
    var menu_order: Int = 0
    var count: Int = 0
}
