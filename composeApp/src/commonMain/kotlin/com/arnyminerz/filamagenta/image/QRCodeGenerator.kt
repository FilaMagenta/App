package com.arnyminerz.filamagenta.image

expect suspend fun generateQRCode(
    content: String,
    cellSize: Int = 25,
    darkColor: Int = 0xFF000000.toInt(),
    brightColor: Int = 0xFFFFFFFF.toInt()
): ByteArray
