package me.gilo.woodroid.models

import kotlinx.serialization.Serializable

@Serializable
class Tag {
    var id: Int = 0
    var name: String? = null
    var slug: String? = null
}
