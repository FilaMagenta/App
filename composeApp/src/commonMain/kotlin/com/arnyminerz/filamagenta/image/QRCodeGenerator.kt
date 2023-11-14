package com.arnyminerz.filamagenta.image

import qrcode.QRCode
import qrcode.color.Colors

object QRCodeGenerator {
    fun generate(
        data: String,
        size: Int = 25,
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
