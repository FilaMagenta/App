package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.cache.ProductOrder
import com.arnyminerz.filamagenta.cache.data.qr.AccountQRCode
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

const val QRTypeOrder = "ORDER"
const val QRTypeAccount = "ACCOUNT"

const val QRTypeIndex = 0

const val OrderQRIndexOrderId = 1
const val OrderQRIndexEventId = 2
const val OrderQRIndexOrderNumber = 3
const val OrderQRIndexCustomerId = 4
const val OrderQRIndexCustomerName = 5
const val OrderQRFieldsCount = 6

@ExperimentalEncodingApi
fun ProductOrder.qrcode(): String {
    val text = "$QRTypeOrder/$id/$eventId/$orderNumber/$customerId/$customerName"
    return Base64.encode(text.encodeToByteArray())
}

@ExperimentalEncodingApi
@ExperimentalUnsignedTypes
fun Account.qrcode(): AccountQRCode {
    return AccountQRCode(this)
}
