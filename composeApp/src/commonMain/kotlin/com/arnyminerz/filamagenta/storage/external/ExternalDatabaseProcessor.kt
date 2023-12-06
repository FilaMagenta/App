package com.arnyminerz.filamagenta.storage.external

interface ExternalDatabaseProcessor {
    fun process(data: ByteArray): ExternalDatabaseResult
}
