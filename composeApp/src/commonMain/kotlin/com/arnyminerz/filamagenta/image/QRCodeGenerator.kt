package com.arnyminerz.filamagenta.image

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import qrcode.QRCode
import qrcode.color.Colors

object QRCodeGenerator {
    fun generate(
        data: String,
        density: Density,
        size: Dp,
        foregroundColor: Int = Colors.BLACK,
        backgroundColor: Int = Colors.WHITE
    ): ByteArray {
        return generate(data, with(density) { size.roundToPx() }, foregroundColor, backgroundColor)
    }

    fun generate(
        data: String,
        size: Int,
        foregroundColor: Int = Colors.BLACK,
        backgroundColor: Int = Colors.WHITE
    ): ByteArray {
        return QRCode.ofSquares()
            .withBackgroundColor(backgroundColor)
            .withColor(foregroundColor)
            .withSize(size)
            .build(data)
            .renderToBytes()
    }
}
