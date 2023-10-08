package com.arnyminerz.filamagenta.network.server.exception

import io.ktor.utils.io.errors.IOException

class ServerException: IOException("The server returned a non-successful code.")
