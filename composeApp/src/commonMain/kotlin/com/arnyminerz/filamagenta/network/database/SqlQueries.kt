package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlQueries(
    val server: String,
    val username: String,
    val password: String,
    val database: String,
    val queries: List<String>
)
