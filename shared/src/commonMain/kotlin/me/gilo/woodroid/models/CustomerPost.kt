package me.gilo.woodroid.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CustomerPost {
    @SerialName("data")
    lateinit var datas: ArrayList<Data>
}
