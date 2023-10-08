package me.gilo.woodroid.models

import kotlinx.serialization.Serializable

@Serializable
class DefaultAttribute {
    var id: Int = 0
    lateinit var name: String
    lateinit var option: String
}
