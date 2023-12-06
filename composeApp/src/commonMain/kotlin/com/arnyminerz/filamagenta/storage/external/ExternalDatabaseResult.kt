package com.arnyminerz.filamagenta.storage.external

data class ExternalDatabaseResult(
    val orders: List<ExternalOrder>,
    val warnings: List<ExternalDatabaseWarning>
)
