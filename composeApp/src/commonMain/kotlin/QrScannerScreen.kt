import cafe.adriel.voyager.core.screen.Screen

expect class QrScannerScreen(
    onQrCodeScanned: (String) -> Unit
): Screen {
    val onQrCodeScanned: (String) -> Unit
}
