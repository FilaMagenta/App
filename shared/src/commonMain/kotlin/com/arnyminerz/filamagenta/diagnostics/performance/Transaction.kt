package com.arnyminerz.filamagenta.diagnostics.performance

expect class Transaction(name: String, operation: String) {
    fun setThrowable(throwable: Throwable)

    fun setStatus(status: TransactionStatus)

    fun finish()
}
