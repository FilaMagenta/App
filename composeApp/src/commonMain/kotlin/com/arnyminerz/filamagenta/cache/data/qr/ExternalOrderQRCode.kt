package com.arnyminerz.filamagenta.cache.data.qr

import com.arnyminerz.filamagenta.cache.data.QRTypeExternal
import com.arnyminerz.filamagenta.storage.external.ExternalOrder
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalEncodingApi
@ExperimentalUnsignedTypes
class ExternalOrderQRCode(
    val name: String,
    val order: String,
    val phone: String,
    val hash: String
): EncryptedQRCode(
    QRTypeExternal,
    name,
    order,
    phone,
    hash
) {
    companion object : QRCodeDecryptor<ExternalOrderQRCode>, QRCodeValidator {
        private const val FIELDS_COUNT = 4

        override fun decrypt(source: String): ExternalOrderQRCode {
            val qrCode = EncryptedQRCode.decrypt(source)
            return ExternalOrderQRCode(qrCode)
        }

        override fun validate(source: String): Boolean {
            val qrCode = EncryptedQRCode.decrypt(source)
            return qrCode.type == QRTypeExternal && qrCode.data.size == FIELDS_COUNT
        }
    }

    @ExperimentalStdlibApi
    constructor(order: ExternalOrder): this(
        order.name, order.order, order.phone, order.hashCode().toHexString()
    )

    constructor(encryptedQRCode: EncryptedQRCode): this(
        encryptedQRCode.data[0], // name
        encryptedQRCode.data[1], // order
        encryptedQRCode.data[2], // phone
        encryptedQRCode.data[3], // hash
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ExternalOrder

        if (name != other.name) return false
        if (order != other.order) return false
        if (phone != other.phone) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + order.hashCode()
        result = 31 * result + phone.hashCode()
        return result
    }
}
