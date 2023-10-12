package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CustomerPost {
    @SerialName("data")
    lateinit var datas: ArrayList<com.arnyminerz.filamagenta.network.woo.models.Data>
}
