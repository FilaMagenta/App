package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlTunnelEntryCollation(
    val lcid: Int,
    val flags: Int,
    val version: Int,
    val sortId: Int,
    val codepage: String
)
