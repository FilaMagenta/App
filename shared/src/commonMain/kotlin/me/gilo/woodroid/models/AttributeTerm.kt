package me.gilo.woodroid.models

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
