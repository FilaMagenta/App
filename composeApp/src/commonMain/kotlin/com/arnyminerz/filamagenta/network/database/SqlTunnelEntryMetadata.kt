package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlTunnelEntryMetadata(
    val userType: Int,
    val flags: Int,
    val dataLength: Int? = null,
    val type: SqlTunnelEntryType,
    val collation: SqlTunnelEntryCollation? = null,
    val precision: Int? = null,
    val scale: Int? = null,
    val colName: String
)
