package com.arnyminerz.filamagenta.ui.state

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.extractMetadata
import com.arnyminerz.filamagenta.cache.data.toEvent
import com.arnyminerz.filamagenta.cache.data.toProductOrder
import com.arnyminerz.filamagenta.cache.database
import com.arnyminerz.filamagenta.network.woo.WooCommerce
import com.arnyminerz.filamagenta.network.woo.models.Metadata
import com.arnyminerz.filamagenta.network.woo.models.Order
import com.arnyminerz.filamagenta.network.woo.update.BatchMetadataUpdate
import com.arnyminerz.filamagenta.network.woo.update.MetadataUpdate
import com.arnyminerz.filamagenta.network.woo.utils.ProductMeta
import com.arnyminerz.filamagenta.network.woo.utils.set
import com.arnyminerz.filamagenta.utils.toEpochMillisecondsString
import io.github.aakira.napier.Napier
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlin.coroutines.cancellation.CancellationException

class EventScreenModel(event: Event): ScreenModel {
    private val _event = MutableStateFlow(event)
    val event: StateFlow<Event> get() = _event

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> get() = _isLoadingEvents

    private val _isLoadingOrders = MutableStateFlow(false)
    val isLoadingOrders: StateFlow<Boolean> get() = _isLoadingOrders

    private val _isDownloadingTickets = MutableStateFlow(false)
    val isDownloadingTickets: StateFlow<Boolean> get() = _isDownloadingTickets

    private val _isUploadingScannedTickets = MutableStateFlow(false)
    val isUploadingScannedTickets: StateFlow<Boolean> get() = _isUploadingScannedTickets

    private val _editingField = MutableStateFlow<EventField<*>?>(null)
    val editingField: StateFlow<EventField<*>?> get() = _editingField

    private val _scanningQr = MutableStateFlow(false)
    val scanningQr: StateFlow<Boolean> get() = _scanningQr

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

    /**
     * Updates [scanningQr] to `true`.
     */
    fun startScanner() {
        _scanningQr.value = true
    }

    /**
     * Updates [scanningQr] to `false`.
     */
    fun stopScanner() {
        _scanningQr.value = false
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

    fun downloadTickets(eventId: Long) = screenModelScope.launch(Dispatchers.IO) {
        try {
            _isDownloadingTickets.emit(true)

            val orders = WooCommerce.Orders.getOrdersForProduct(eventId.toInt())
            for (order: Order in orders) {
                order.toProductOrder().forEach(Cache::insertOrUpdateAdminTicket)
            }
        } finally {
            _isDownloadingTickets.emit(false)
        }
    }

    fun deleteTickets(eventId: Long) = screenModelScope.launch(Dispatchers.IO) {
        database.adminTicketsQueries.deleteByEventId(eventId)
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
