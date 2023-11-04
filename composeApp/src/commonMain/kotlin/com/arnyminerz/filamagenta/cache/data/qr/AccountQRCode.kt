package com.arnyminerz.filamagenta.cache.data.qr

import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.cache.data.QRTypeAccount
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalUnsignedTypes
@ExperimentalEncodingApi
class AccountQRCode(val accountName: String, val idSocio: Long, val customerId: Long) : EncryptedQRCode(
    QRTypeAccount,
    accountName,
    idSocio.toString(),
    customerId.toString()
) {
    companion object : QRCodeDecryptor<AccountQRCode>, QRCodeValidator {
        private const val FIELDS_COUNT = 3

        override fun decrypt(source: String): AccountQRCode {
            val qrCode = EncryptedQRCode.decrypt(source)
            return AccountQRCode(qrCode)
        }

        override fun validate(source: String): Boolean {
            val qrCode = EncryptedQRCode.decrypt(source)
            return qrCode.type == QRTypeAccount && qrCode.data.size == FIELDS_COUNT
        }
    }

    constructor(account: Account, idSocio: Long, customerId: Long) : this(account.name, idSocio, customerId)

    constructor(qrCode: EncryptedQRCode) : this(qrCode.data[0], qrCode.data[1].toLong(), qrCode.data[2].toLong())
}
