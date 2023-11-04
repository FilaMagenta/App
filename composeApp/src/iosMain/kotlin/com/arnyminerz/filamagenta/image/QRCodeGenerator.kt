package com.arnyminerz.filamagenta.image

import com.arnyminerz.filamagenta.network.httpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.parameters
import io.ktor.util.toByteArray
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.copyAndClose

@OptIn(ExperimentalStdlibApi::class)
actual suspend fun generateQRCode(content: String, cellSize: Int, darkColor: Int, brightColor: Int): ByteArray {
    httpClient.get(
        URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = "api.qrserver.com",
            pathSegments = listOf("v1", "create-qr-code"),
            parameters = parameters {
                set("data", content)
                set("format", "jpg")
                set("margin", "0")
                set("color", darkColor.toHexString())
                set("bgcolor", brightColor.toHexString())
            }
        ).build()
    ).apply {
        val channel = ByteChannel()
        bodyAsChannel().copyAndClose(channel)
        return channel.toByteArray()
    }
}
