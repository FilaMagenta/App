package com.arnyminerz.filamagenta.diagnostics.performance

// TODO - performance measurements currently not available for iOS
actual class Transaction actual constructor(name: String, operation: String) {
    actual fun setThrowable(throwable: Throwable) {
    }

    actual fun setStatus(status: TransactionStatus) {
    }

    actual fun finish() {
    }
}
