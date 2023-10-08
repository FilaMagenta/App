package me.gilo.woodroid.models

import kotlinx.serialization.Serializable

@Serializable
class Download  {
    lateinit var id: String
    lateinit var name: String
    lateinit var file: String
}
