package com.arnyminerz.filamagenta.image

expect object QRCodeGenerator {
    suspend fun generate(
        content: String,
        cellSize: Int = 25,
        darkColor: Int = 0xffffff,
        brightColor: Int = 0x000000
    ): ByteArray
}
