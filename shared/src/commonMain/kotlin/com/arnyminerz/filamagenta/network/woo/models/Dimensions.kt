package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
data class Dimensions(
    val length: String,
    val width: String,
    val height: String
)
