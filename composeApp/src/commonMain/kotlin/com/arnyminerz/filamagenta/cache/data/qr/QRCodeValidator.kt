package com.arnyminerz.filamagenta.cache.data.qr

interface QRCodeValidator {
    /**
     * Checks whether the QR code read is valid for the current class.
     *
     * @param source The QR code contents in raw Base64.
     *
     * @return `true` if the contents are valid, `false` otherwise.
     */
    fun validate(source: String): Boolean
}
