package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class Metadata(
    var id: Int,
    val key: String,
    val value: String
)
