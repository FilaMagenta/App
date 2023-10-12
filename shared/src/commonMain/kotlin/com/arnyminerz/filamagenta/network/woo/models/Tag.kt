package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class Tag {
    var id: Int = 0
    var name: String? = null
    var slug: String? = null
}
