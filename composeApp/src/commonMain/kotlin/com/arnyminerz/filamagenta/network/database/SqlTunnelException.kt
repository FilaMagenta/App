package com.arnyminerz.filamagenta.network.database

import io.ktor.http.HttpStatusCode

/**
 * An error returned by the SQLServer tunnel.
 */
class SqlTunnelException(
    val status: HttpStatusCode,
    message: String? = null
): Exception("SQL Tunnel returned an error (${status.value}): $message")
