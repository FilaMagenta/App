package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlTunnelEntryMetadata(
    val userType: Int,
    val flags: Int,
    val dataLength: Int?,
    val type: SqlTunnelEntryType,
    val collation: SqlTunnelEntryCollation?,
    val precision: Int?,
    val scale: Int?,
    val colName: String
)
