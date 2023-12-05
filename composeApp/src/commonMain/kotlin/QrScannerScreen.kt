import cafe.adriel.voyager.core.screen.Screen

expect class QrScannerScreen(
    onQrCodeScanned: ((String) -> Unit)?,
    eventId: Long?
): Screen {
    val onQrCodeScanned: ((String) -> Unit)?
    val eventId: Long?
}
