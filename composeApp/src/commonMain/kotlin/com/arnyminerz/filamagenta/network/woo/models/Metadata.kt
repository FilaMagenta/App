package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    // var id: Int,
    val key: String,
    val value: String
)
