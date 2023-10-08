package com.arnyminerz.filamagenta.network.server.exception

import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException

class ServerException(
    code: HttpStatusCode,
    body: String
): IOException("The server returned a non-successful code ($code): $body")
