package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
class SqlTunnelResponse(
    val successful: Boolean,
    val results: List<List<SqlTunnelEntry>>? = null,
    val error: SqlTunnelError? = null
)
