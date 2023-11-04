package com.arnyminerz.filamagenta.cache.data.qr

import com.arnyminerz.filamagenta.BuildKonfig
import com.ionspin.kotlin.crypto.secretbox.SecretBox
import com.ionspin.kotlin.crypto.secretbox.SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey
import com.ionspin.kotlin.crypto.util.decodeFromUByteArray
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalUnsignedTypes
@ExperimentalEncodingApi
open class EncryptedQRCode(
    val type: String,
    val data: List<String>
) {
    companion object {
        /**
         * Decrypts some QR data in Base64 into an [EncryptedQRCode].
         *
         * @throws IllegalArgumentException If [source] is not formatted correctly in Base64.
         * @throws SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey If the data could not be decrypted.
         */
        fun decrypt(source: String): EncryptedQRCode {
            val message = Base64.decode(source)
            val key = BuildKonfig.QrCodeKey.encodeToUByteArray()
            val nonce = BuildKonfig.QrCodeNonce.encodeToUByteArray()

            val decrypted = SecretBox.openEasy(message.toUByteArray(), nonce, key).decodeFromUByteArray()
            val pieces = decrypted.split('/')

            return EncryptedQRCode(
                pieces[0],
                pieces.subList(1, pieces.size)
            )
        }
    }

    @Suppress("SpreadOperator")
    constructor(type: String, vararg data: String): this(type, listOf(*data))

    val message = "$type/${data.joinToString("/")}"

    /**
     * Returns the QR data as an encrypted string.
     *
     * @return The encrypted QR code data as a Base64 string.
     */
    fun encrypt(): String {
        val key = BuildKonfig.QrCodeKey.encodeToUByteArray()
        val nonce = BuildKonfig.QrCodeNonce.encodeToUByteArray()

        val encrypted = SecretBox.easy(
            message = message.encodeToUByteArray(),
            nonce = nonce,
            key = key
        )

        return Base64.encode(encrypted.toByteArray())
    }
}
