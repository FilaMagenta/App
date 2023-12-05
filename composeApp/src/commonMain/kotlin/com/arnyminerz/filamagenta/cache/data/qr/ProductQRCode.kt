package com.arnyminerz.filamagenta.cache.data.qr

import com.arnyminerz.filamagenta.cache.AdminTickets
import com.arnyminerz.filamagenta.cache.ProductOrder
import com.arnyminerz.filamagenta.cache.data.QRTypeOrder
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalUnsignedTypes
@ExperimentalEncodingApi
class ProductQRCode(
    val eventId: Long,
    val orderId: Long,
    val orderNumber: String,
    val customerId: Long,
    val customerName: String
) : EncryptedQRCode(
    QRTypeOrder,
    eventId.toString(),
    orderId.toString(),
    orderNumber,
    customerId.toString(),
    customerName
) {
    companion object : QRCodeDecryptor<ProductQRCode>, QRCodeValidator {
        private const val FIELDS_COUNT = 5

        override fun decrypt(source: String): ProductQRCode {
            val qrCode = EncryptedQRCode.decrypt(source)
            return ProductQRCode(qrCode)
        }

        override fun validate(source: String): Boolean {
            val qrCode = EncryptedQRCode.decrypt(source)
            return qrCode.type == QRTypeOrder && qrCode.data.size == FIELDS_COUNT
        }
    }

    constructor(order: ProductOrder) : this(
        order.eventId,
        order.id,
        order.orderNumber,
        order.customerId,
        order.customerName
    )

    constructor(qrCode: EncryptedQRCode) : this(
        qrCode.data[0].toLong(),
        qrCode.data[1].toLong(),
        qrCode.data[2],
        qrCode.data[3].toLong(),
        qrCode.data[4]
    )

    constructor(ticket: AdminTickets) : this(
        ticket.eventId,
        ticket.orderId,
        ticket.orderNumber,
        ticket.customerId,
        ticket.customerName
    )
}
