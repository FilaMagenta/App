package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.ProductOrder
import com.arnyminerz.filamagenta.cache.data.qr.AccountQRCode
import com.arnyminerz.filamagenta.cache.data.qr.ProductQRCode
import kotlin.io.encoding.ExperimentalEncodingApi

const val QRTypeOrder = "ORDER"
const val QRTypeAccount = "ACCOUNT"

@ExperimentalEncodingApi
@ExperimentalUnsignedTypes
fun ProductOrder.qrcode(): ProductQRCode {
    return ProductQRCode(this)
}

/**
 * Obtains the QR code associated with the account.
 *
 * **The account's idSocio and customerId must have been set, otherwise a [NullPointerException] will be thrown**
 *
 * @throws NullPointerException if the account doesn't have a valid `idSocio` or `customerId` in [accounts].
 */
@ExperimentalEncodingApi
@ExperimentalUnsignedTypes
fun Account.qrcode(): AccountQRCode {
    val idSocio = accounts.getIdSocio(this)!!.toLong()
    val customerId = accounts.getCustomerId(this)!!.toLong()
    return AccountQRCode(this, idSocio, customerId)
}
