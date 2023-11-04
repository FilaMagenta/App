package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlTunnelEntry(
    val value: String?,
    val metadata: SqlTunnelEntryMetadata
)
