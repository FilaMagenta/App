package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlTunnelEntryType(
    val id: Int,
    val type: String,
    val name: String,
    val maximumLength: Int? = null
)
