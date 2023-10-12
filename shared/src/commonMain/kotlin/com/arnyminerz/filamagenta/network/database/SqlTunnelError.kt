package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlTunnelError(
    val message: String? = null
)
