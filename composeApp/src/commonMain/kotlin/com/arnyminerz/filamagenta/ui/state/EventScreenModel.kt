package com.arnyminerz.filamagenta.ui.state

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.extractMetadata
import com.arnyminerz.filamagenta.cache.data.qr.ExternalOrderQRCode
import com.arnyminerz.filamagenta.cache.data.qr.ProductQRCode
import com.arnyminerz.filamagenta.cache.data.toEvent
import com.arnyminerz.filamagenta.cache.data.toProductOrder
import com.arnyminerz.filamagenta.cache.database
import com.arnyminerz.filamagenta.data.QrCodeScanResult
import com.arnyminerz.filamagenta.device.FSInformation
import com.arnyminerz.filamagenta.image.QRCodeGenerator
import com.arnyminerz.filamagenta.image.QRCodeValidator
import com.arnyminerz.filamagenta.network.woo.WooCommerce
import com.arnyminerz.filamagenta.network.woo.models.Metadata
import com.arnyminerz.filamagenta.network.woo.models.Order
import com.arnyminerz.filamagenta.network.woo.update.BatchMetadataUpdate
import com.arnyminerz.filamagenta.network.woo.update.MetadataUpdate
import com.arnyminerz.filamagenta.network.woo.utils.ProductMeta
import com.arnyminerz.filamagenta.network.woo.utils.set
import com.arnyminerz.filamagenta.storage.external.ExternalOrder
import com.arnyminerz.filamagenta.utils.toEpochMillisecondsString
import com.oldguy.common.io.ByteBuffer
import com.oldguy.common.io.File
import com.oldguy.common.io.FileMode
import com.oldguy.common.io.RawFile
import com.oldguy.common.io.ZipFile
import com.oldguy.common.io.use
import io.github.aakira.napier.Napier
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class EventScreenModel(eventId: Long): ScreenModel {
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> get() = _event

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> get() = _isLoadingEvents

    private val _isLoadingOrders = MutableStateFlow(false)
    val isLoadingOrders: StateFlow<Boolean> get() = _isLoadingOrders

    private val _isDownloadingTickets = MutableStateFlow(false)
    val isDownloadingTickets: StateFlow<Boolean> get() = _isDownloadingTickets

    private val _isUploadingScannedTickets = MutableStateFlow(false)
    val isUploadingScannedTickets: StateFlow<Boolean> get() = _isUploadingScannedTickets

    private val _isExportingTickets = MutableStateFlow(false)
    val isExportingTickets: StateFlow<Boolean> get() = _isExportingTickets

    private val _scanResult = MutableStateFlow<QrCodeScanResult?>(null)
    val scanResult: StateFlow<QrCodeScanResult?> get() = _scanResult

    private val _editingField = MutableStateFlow<EventField<*>?>(null)
    val editingField: StateFlow<EventField<*>?> get() = _editingField

    init {
        CoroutineScope(Dispatchers.IO).launch {
            database.eventQueries.getById(eventId).executeAsOne().let { _event.emit(it) }
        }
    }

    /**
     * Starts editing the given [field] for the currently selected event.
     */
    fun edit(field: EventField<*>) {
        screenModelScope.launch { _editingField.emit(field) }
    }

    /**
     * Clears the value of [editingField].
     */
    fun cancelEdit() {
        screenModelScope.launch { _editingField.emit(null) }
    }

    fun dismissScanResult() {
        _scanResult.value = null
    }

    fun validateQRCode(data: String) = screenModelScope.launch(Dispatchers.IO) {
        QRCodeValidator.validateQRCode(data, _scanResult, event.value)
    }

    fun fetchOrders(productId: Int) = screenModelScope.launch(Dispatchers.IO) {
        // Wait until another thread finishes loading
        while (_isLoadingOrders.value) {
            delay(1)
        }

        try {
            _isLoadingOrders.emit(true)

            val customerId = getOrFetchCustomerId()

            Napier.d("Fetching orders for Product#$productId made by Customer#$customerId")
            val orders = WooCommerce.Orders.getOrdersForProductAndCustomer(customerId, productId)
            Napier.i("Got ${orders.size} orders for Product#$productId and Customer#$customerId. Updating cache...")

            orders.flatMap(Order::toProductOrder)
                .forEach { order ->
                    Napier.d("Inserting Order#${order.id}")
                    Cache.insertOrUpdate(order)
                }
        } catch (_: CancellationException) {
            Napier.w("The orders fetching for Product#$productId was cancelled.")
        } finally {
            _isLoadingOrders.emit(false)
        }
    }

    fun downloadTickets(
        eventId: Long,
        runIfEmpty: () -> Unit
    ) = screenModelScope.launch(Dispatchers.IO) {
        try {
            _isDownloadingTickets.emit(true)

            val orders = WooCommerce.Orders.getOrdersForProduct(eventId.toInt())
            if (orders.isEmpty()) {
                runIfEmpty()
            } else for (order: Order in orders) {
                order.toProductOrder().forEach(Cache::insertOrUpdateAdminTicket)
            }
        } finally {
            _isDownloadingTickets.emit(false)
        }
    }

    fun deleteTickets(eventId: Long) = screenModelScope.launch(Dispatchers.IO) {
        database.adminTicketsQueries.deleteByEventId(eventId)
    }

    @OptIn(ExperimentalEncodingApi::class, ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)
    fun exportTickets(eventId: Long, externalOrders: List<ExternalOrder>) = screenModelScope.launch(Dispatchers.IO) {
        _isExportingTickets.emit(true)

        val ticketsDirectory = FSInformation.exportedTicketsDirectory()
        if (!ticketsDirectory.exists) {
            ticketsDirectory.makeDirectory()
        }

        if (externalOrders.isNotEmpty()) {
            Napier.i { "Exporting external orders (${externalOrders.size}) to $ticketsDirectory..." }
            for (order in externalOrders) {
                val data = ExternalOrderQRCode(order).encrypt()
                val image = QRCodeGenerator.generate(data)
                val file = File(ticketsDirectory, "${order.name}-${order.phone}.png")
                Napier.i { "Writing order ${order.order} into ${file.path}" }
                RawFile(file, FileMode.Write).use { output ->
                    output.write(
                        ByteBuffer(image)
                    )
                }
            }
        } else {
            val tickets = database.adminTicketsQueries.getByEventId(eventId).executeAsList()
            Napier.i { "Exporting all tickets (${tickets.size}) to $ticketsDirectory..." }
            for (ticket in tickets) {
                val data = ProductQRCode(ticket).encrypt()
                val image = QRCodeGenerator.generate(data)
                val file = File(ticketsDirectory, "${ticket.customerName}-${ticket.orderId}.png")
                Napier.i { "Writing ticket ${ticket.orderId} into ${file.path}" }
                RawFile(file, FileMode.Write).use { output ->
                    output.write(
                        ByteBuffer(image)
                    )
                }
            }
        }

        // Create zip file
        Napier.i { "Creating zip file..." }
        val zip = File(ticketsDirectory, "exported.zip").also { if (it.exists) it.delete() }
        val generatedFiles = ticketsDirectory.listFiles.filterNot { it.isDirectory }
        Napier.d { "Adding ${generatedFiles.size} files to the zip file..." }
        ZipFile(zip, FileMode.Write).use {
            it.zipDirectory(
                ticketsDirectory,
                shallow = false
            ) { name -> name.endsWith(".png") }
        }

        _isExportingTickets.emit(false)
    }

    fun syncScannedTickets(eventId: Long) = screenModelScope.launch(Dispatchers.IO) {
        try {
            _isUploadingScannedTickets.emit(true)

            val update = mutableListOf<BatchMetadataUpdate.Entry>()

            val tickets = database.adminTicketsQueries.getByEventId(eventId).executeAsList()

            for (ticket in tickets) {
                // First get the order associated with the event
                val order = database.productOrderQueries.getById(ticket.orderId).executeAsOneOrNull() ?: continue
                // Now compare the update time of the order with the one stored at the ticket, they should match
                if (order.lastUpdate < ticket.lastUpdate) {
                    // If the ticket was updated after the order, add for the update
                    val existingMeta = DefaultJson.decodeFromString<List<Metadata>>(ticket._cache_meta_data)
                        .set("validated", "true")

                    update.add(
                        BatchMetadataUpdate.Entry(
                            ticket.orderId,
                            existingMeta
                        )
                    )
                }
            }

            WooCommerce.Orders.batchUpdateMetadata(
                BatchMetadataUpdate(update)
            )
        } finally {
            _isUploadingScannedTickets.emit(false)
        }
    }

    /**
     * Requests the server to store the update made at [field].
     */
    fun <T> performUpdate(event: Event, field: EventField<T>): Job {
        return screenModelScope.launch(Dispatchers.IO) {
            val rawValue = field.value
            val (key, value) = when (field) {
                is EventField.Name -> throw UnsupportedOperationException("Cannot change names right now")
                is EventField.Date -> ProductMeta.EVENT_DATE to (rawValue as LocalDateTime).toEpochMillisecondsString()
                is EventField.Type -> ProductMeta.CATEGORY to (rawValue as EventType).name
            }

            val product = WooCommerce.Products.update(
                event.id,
                MetadataUpdate(
                    event.extractMetadata().set(key, value)
                )
            )
            // fixme - should include variations
            val newEvent = product.toEvent(emptyList())

            Cache.insertOrUpdate(newEvent)
            _event.emit(newEvent)
        }
    }
}
