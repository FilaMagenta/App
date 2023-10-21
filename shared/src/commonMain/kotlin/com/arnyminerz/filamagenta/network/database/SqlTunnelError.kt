package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlTunnelError(
    val message: String? = null,
    val code: String? = null,
    val number: Int? = null,
    val state: Int? = null,
    val `class`: Int? = null,
    val serverName: String? = null,
    val procName: String? = null,
    val lineNumber: Int? = null
)
