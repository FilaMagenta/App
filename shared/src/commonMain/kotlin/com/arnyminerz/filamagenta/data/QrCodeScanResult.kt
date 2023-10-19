package com.arnyminerz.filamagenta.data

sealed class QrCodeScanResult {
    data object Invalid: QrCodeScanResult()

    data object AlreadyUsed: QrCodeScanResult()

    data object NotViewingEvent: QrCodeScanResult()

    data object TicketListNotDownloaded: QrCodeScanResult()

    data class Success(
        val customerName: String,
        val orderNumber: String
    ): QrCodeScanResult()
}
