package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.ProductOrder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

const val QRTypeOrder = "ORDER"

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
