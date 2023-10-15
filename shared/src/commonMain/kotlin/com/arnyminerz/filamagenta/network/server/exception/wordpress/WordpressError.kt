package com.arnyminerz.filamagenta.network.server.exception.wordpress

import kotlinx.serialization.Serializable

@Serializable
class WordpressError(
    val code: String,
    val message: String,
    val data: WordpressErrorData
)
