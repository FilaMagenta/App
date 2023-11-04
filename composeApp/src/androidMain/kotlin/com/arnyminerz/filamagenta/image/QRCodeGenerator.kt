package com.arnyminerz.filamagenta.image

import io.github.g0dkar.qrcode.QRCode

actual suspend fun generateQRCode(
    content: String,
    cellSize: Int,
    darkColor: Int,
    brightColor: Int
): ByteArray {
    return QRCode(content)
        .render(cellSize = cellSize, darkColor = darkColor, brightColor = brightColor)
        .getBytes()
}
