package com.arnyminerz.filamagenta.diagnostics.performance

enum class TransactionStatus {

    /** Not an error, returned on success. */
    OK,

    /** The operation was cancelled, typically by the caller. */
    CANCELLED,

    /**
     * Some invariants expected by the underlying system have been broken. This code is reserved for serious errors.
     */
    INTERNAL_ERROR,

    /** An unknown error raised by APIs that don't return enough error information. */
    UNKNOWN,

    /** An unknown error raised by APIs that don't return enough error information. */
    UNKNOWN_ERROR,

    /** The client specified an invalid argument. */
    INVALID_ARGUMENT,

    /** The deadline expired before the operation could succeed. */
    DEADLINE_EXCEEDED,

    /** Content was not found or request was denied for an entire class of users. */
    NOT_FOUND,

    /** The entity attempted to be created already exists */
    ALREADY_EXISTS,

    /** The caller doesn't have permission to execute the specified operation. */
    PERMISSION_DENIED,

    /** The resource has been exhausted e.g. per-user quota exhausted, file system out of space. */
    RESOURCE_EXHAUSTED,

    /** The client shouldn't retry until the system state has been explicitly handled. */
    FAILED_PRECONDITION,

    /** The operation was aborted. */
    ABORTED,

    /** The operation was attempted past the valid range e.g. seeking past the end of a file. */
    OUT_OF_RANGE,

    /** The operation is not implemented or is not supported/enabled for this operation. */
    UNIMPLEMENTED,

    /** The service is currently available e.g. as a transient condition. */
    UNAVAILABLE,

    /** Unrecoverable data loss or corruption. */
    DATA_LOSS,

    /** The requester doesn't have valid authentication credentials for the operation. */
    UNAUTHENTICATED
}
