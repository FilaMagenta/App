package com.arnyminerz.filamagenta.diagnostics.performance

import io.sentry.Sentry
import io.sentry.SpanStatus

actual class Transaction actual constructor(name: String, operation: String) {
    private val sentryTransaction = Sentry.startTransaction(name, operation)

    actual fun setThrowable(throwable: Throwable) {
        sentryTransaction.throwable = throwable
    }

    actual fun setStatus(status: TransactionStatus) {
        sentryTransaction.status = when(status) {
            TransactionStatus.OK -> SpanStatus.OK
            TransactionStatus.CANCELLED -> SpanStatus.CANCELLED
            TransactionStatus.INTERNAL_ERROR -> SpanStatus.INTERNAL_ERROR
            TransactionStatus.UNKNOWN -> SpanStatus.UNKNOWN
            TransactionStatus.UNKNOWN_ERROR -> SpanStatus.UNKNOWN_ERROR
            TransactionStatus.INVALID_ARGUMENT -> SpanStatus.INVALID_ARGUMENT
            TransactionStatus.DEADLINE_EXCEEDED -> SpanStatus.DEADLINE_EXCEEDED
            TransactionStatus.NOT_FOUND -> SpanStatus.NOT_FOUND
            TransactionStatus.ALREADY_EXISTS -> SpanStatus.ALREADY_EXISTS
            TransactionStatus.PERMISSION_DENIED -> SpanStatus.PERMISSION_DENIED
            TransactionStatus.RESOURCE_EXHAUSTED -> SpanStatus.RESOURCE_EXHAUSTED
            TransactionStatus.FAILED_PRECONDITION -> SpanStatus.FAILED_PRECONDITION
            TransactionStatus.ABORTED -> SpanStatus.ABORTED
            TransactionStatus.OUT_OF_RANGE -> SpanStatus.OUT_OF_RANGE
            TransactionStatus.UNIMPLEMENTED -> SpanStatus.UNIMPLEMENTED
            TransactionStatus.UNAVAILABLE -> SpanStatus.UNAVAILABLE
            TransactionStatus.DATA_LOSS -> SpanStatus.DATA_LOSS
            TransactionStatus.UNAUTHENTICATED -> SpanStatus.UNAUTHENTICATED
        }
    }

    actual fun finish() {
        sentryTransaction.finish()
    }
}
