package com.arnyminerz.filamagenta.cache.data.qr

import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.cache.data.QRTypeAccount
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalUnsignedTypes
@ExperimentalEncodingApi
class AccountQRCode(accountName: String): EncryptedQRCode(QRTypeAccount, accountName) {
    companion object: QRCodeDecryptor<AccountQRCode> {
        override fun decrypt(source: String): AccountQRCode {
            val qrCode = EncryptedQRCode.decrypt(source)
            return AccountQRCode(qrCode)
        }
    }

    constructor(account: Account): this(account.name)

    constructor(qrCode: EncryptedQRCode): this(qrCode.data[0])
}
