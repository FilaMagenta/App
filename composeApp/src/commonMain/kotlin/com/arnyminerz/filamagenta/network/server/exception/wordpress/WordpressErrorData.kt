package com.arnyminerz.filamagenta.network.server.exception.wordpress

import kotlinx.serialization.Serializable

@Serializable
class WordpressErrorData(
    val status: Int
)
