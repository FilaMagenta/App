package com.arnyminerz.filamagenta.network.server.exception

import com.arnyminerz.filamagenta.network.server.exception.wordpress.WordpressError
import io.ktor.utils.io.errors.IOException

class WordpressException(
    val path: String,
    val error: WordpressError
): IOException(
    "The server returned an error at $path. " +
            "Code: ${error.code}. " +
            "Status: ${error.data.status}. " +
            "Message: ${error.message}"
)
