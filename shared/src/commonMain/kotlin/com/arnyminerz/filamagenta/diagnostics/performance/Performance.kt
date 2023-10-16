package com.arnyminerz.filamagenta.diagnostics.performance

object Performance {
    fun measure(name: String, operation: String): Transaction {
        return Transaction(name, operation)
    }
}
