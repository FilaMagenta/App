package com.arnyminerz.filamagenta.cache.data.qr

import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalUnsignedTypes
@ExperimentalEncodingApi
interface QRCodeDecryptor <Type: EncryptedQRCode> {
    fun decrypt(source: String): Type
}
