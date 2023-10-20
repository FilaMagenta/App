package com.arnyminerz.filamagenta.image

import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.qr.AccountQRCode
import com.arnyminerz.filamagenta.cache.data.qr.ProductQRCode
import com.arnyminerz.filamagenta.cache.database
import com.arnyminerz.filamagenta.data.QrCodeScanResult
import com.ionspin.kotlin.crypto.secretbox.SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey
import io.github.aakira.napier.Napier
import io.ktor.http.Url
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

object QRCodeValidator {
    @OptIn(ExperimentalEncodingApi::class, ExperimentalUnsignedTypes::class)
    private suspend fun validateAccountQRCode(
        qrCode: AccountQRCode,
        event: Event,
        result: MutableStateFlow<QrCodeScanResult?>
    ) {
        Napier.v("Checking if tickets are downloaded...")
        val count = database.adminTicketsQueries.countByEventId(event.id).executeAsOne()
        if (count <= 0) {
            Napier.e("There aren't any tickets downloaded for event $event")
            result.emit(QrCodeScanResult.TicketListNotDownloaded)
            return
        }

        Napier.v("Retrieving tickets by event id: ${event.id} and customer id: ${qrCode.customerId}...")
        val tickets = database.adminTicketsQueries
            .getByEventIdAndCustomerId(event.id, qrCode.customerId)
            // Get all the tickets the customer has for the event
            .executeAsList()
        Napier.v("There are ${tickets.size} tickets for this event for the customer.")
        if (tickets.isEmpty()) {
            Napier.e("Customer doesn't have any tickets for this event.")
            result.emit(QrCodeScanResult.Invalid)
            return
        }

        val nonValidatedTickets = tickets.filter { !it.isValidated }
        Napier.v("From those, ${nonValidatedTickets.size} are not validated.")
        if (nonValidatedTickets.isEmpty()) {
            Napier.e("There aren't any tickets to validate left. Notifying reused")
            result.emit(QrCodeScanResult.AlreadyUsed)
            return
        }

        Napier.v("Validating the first non-validated QR code...")
        val ticket = nonValidatedTickets.first()
        withContext(Dispatchers.Main) {
            Cache.updateIsValidated(ticket.orderId, true)
        }

        result.emit(
            QrCodeScanResult.Success(ticket.customerName, ticket.orderNumber)
        )
    }

    @OptIn(ExperimentalEncodingApi::class, ExperimentalUnsignedTypes::class)
    suspend fun validateQRCode(source: String, result: MutableStateFlow<QrCodeScanResult?>, event: Event?) {
        try {
            if (source.startsWith("app://filamagenta")) {
                Napier.i("Got an NFC tag.")

                Napier.v("Parsing the given source into an Url: $source")
                val url = Url(source)
                val query = url.encodedQuery
                    .split("&")
                    .associate {
                        val pairs = it.split('=')
                        pairs[0] to pairs[1]
                    }

                Napier.v("Checking if the url leads to an account...")
                if (!url.pathSegments.contains("account")) {
                    Napier.e("Got an unknown NFC tag.")
                    return
                }

                Napier.v("Checking if the url stored at the NFC tag has a code...")
                if (!query.containsKey("code")) {
                    Napier.e("NFC tag didn't have any code.")
                    result.emit(QrCodeScanResult.Invalid)
                    return
                }
                val code = query.getValue("code")

                Napier.v("Checking if there's an event currently open...")
                if (event == null) {
                    Napier.e("Currently not viewing any events. Ignoring scanned code...")
                    result.emit(QrCodeScanResult.NotViewingEvent)
                    return
                }

                Napier.v("There's a loaded event. Decrypting the code...")
                val qrCode = AccountQRCode.decrypt(code)

                validateAccountQRCode(qrCode, event, result)
            } else if (AccountQRCode.validate(source)) {
                Napier.i("Got an account QR code.")
                val qrCode = AccountQRCode.decrypt(source)

                Napier.v("Checking if there's an event currently open...")
                if (event == null) {
                    Napier.e("Currently not viewing any events. Ignoring scanned code...")
                    result.emit(QrCodeScanResult.NotViewingEvent)
                    return
                }

                validateAccountQRCode(qrCode, event, result)
            } else if (ProductQRCode.validate(source)) {
                Napier.i("Got a product QR code.")
                val qrCode = ProductQRCode.decrypt(source)

                Napier.i("Got valid QR, checking if stored...")
                val ticket = database.adminTicketsQueries.getById(qrCode.orderId).executeAsOneOrNull()
                if (ticket == null) {
                    Napier.i("QR is not stored locally")
                    result.emit(QrCodeScanResult.Invalid)
                    return
                }

                Napier.i("QR is stored, checking if reused...")

                if (ticket.isValidated) {
                    result.emit(QrCodeScanResult.AlreadyUsed)
                } else {
                    withContext(Dispatchers.Main) {
                        Cache.updateIsValidated(qrCode.orderId, true)
                    }

                    result.emit(
                        QrCodeScanResult.Success(qrCode.customerName, qrCode.orderNumber)
                    )
                }
            } else {
                Napier.i("Got invalid QR")
                result.emit(QrCodeScanResult.Invalid)
            }
        } catch (e: SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey) {
            Napier.e("The scanned QR code or NFC tag is corrupted", throwable = e)
            result.emit(QrCodeScanResult.Invalid)
        }
    }
}
