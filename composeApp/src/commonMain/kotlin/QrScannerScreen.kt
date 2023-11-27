import cafe.adriel.voyager.core.screen.Screen

expect class QrScannerScreen: Screen {
    val onQrCodeScanned: (String) -> Unit
}
