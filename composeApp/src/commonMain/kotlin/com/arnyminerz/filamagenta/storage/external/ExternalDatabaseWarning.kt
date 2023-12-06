package com.arnyminerz.filamagenta.storage.external

sealed class ExternalDatabaseWarning(val message: String) {
    data object InconsistentCount : ExternalDatabaseWarning(
        "The orders count specified in the database doesn't match the one found after processing."
    )

    data object Empty : ExternalDatabaseWarning(
        "Processing could not find any orders."
    )
}
