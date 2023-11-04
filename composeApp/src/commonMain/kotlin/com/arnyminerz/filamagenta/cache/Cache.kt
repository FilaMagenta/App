package com.arnyminerz.filamagenta.cache

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.arnyminerz.filamagenta.cache.data.hasBeenValidated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

object Cache {
    val events: Flow<List<Event>> =
        database.eventQueries
            .getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)

    val transactions: Query<AccountTransaction> = database.accountTransactionQueries.getAll()

    fun ordersForEvent(eventId: Long): Query<ProductOrder> = database.productOrderQueries.getByEventId(eventId)

    fun adminTicketsForEvent(eventId: Long) = database.adminTicketsQueries.getByEventId(eventId)

    @Composable
    fun <RowType : Any> Query<RowType>.collectListAsState(): State<List<RowType>> {
        val flow = remember { MutableStateFlow(executeAsList()) }

        DisposableEffect(this) {
            val listener = Query.Listener {
                runBlocking {
                    flow.emit(executeAsList())
                }
            }

            addListener(listener)

            onDispose {
                removeListener(listener)
            }
        }

        return flow.collectAsState()
    }

    fun insertOrUpdate(event: Event) {
        val element = database.eventQueries
            .getById(event.id)
            .executeAsOneOrNull()
        if (element == null) {
            // insert
            database.eventQueries.insert(
                event.id,
                event.name,
                event.date,
                event.type,
                event.variations,
                event._cache_meta_data
            )
        } else {
            // update
            database.eventQueries.update(
                event.name,
                event.date,
                event.type,
                event.variations,
                event._cache_meta_data,
                event.id
            )
        }
    }

    /**
     * Synchronizes the local cache with the given list of events. This includes creating, updating and deleting.
     */
    fun synchronizeEvents(events: List<Event>) {
        val ids = arrayListOf<Long>()
        for (item in events) {
            insertOrUpdate(item)
            ids.add(item.id)
        }
        // Now remove all the elements from the database which are not inside ids
        database.eventQueries.retainById(ids)
    }

    /**
     * Inserts the given [transaction] if it's not cached, or updates the cache entry otherwise.
     */
    fun insertOrUpdate(transaction: AccountTransaction) {
        val element = database.accountTransactionQueries
            .getById(transaction.id)
            .executeAsOneOrNull()
        with(transaction) {
            if (element == null) {
                // insert
                database.accountTransactionQueries.insert(id, date, description, units, cost, income)
            } else {
                // update
                database.accountTransactionQueries.update(date, description, units, cost, income, id)
            }
        }
    }

    fun synchronizeTransactions(transactions: List<AccountTransaction>) {
        val ids = arrayListOf<Long>()
        for (transaction in transactions) {
            insertOrUpdate(transaction)
            ids.add(transaction.id)
        }
        // Now remove all the elements from the database which are not inside ids
        database.accountTransactionQueries.retainById(ids)
    }

    fun insertOrUpdate(order: ProductOrder) {
        val element = database.productOrderQueries
            .getById(order.id)
            .executeAsOneOrNull()
        with(order) {
            if (element == null) {
                // insert
                database.productOrderQueries.insert(
                    id,
                    lastUpdate,
                    eventId,
                    orderNumber,
                    date,
                    customerId,
                    customerName,
                    _cache_meta_data
                )
            } else {
                // update
                database.productOrderQueries.update(
                    lastUpdate,
                    eventId,
                    orderNumber,
                    date,
                    customerId,
                    customerName,
                    _cache_meta_data,
                    id
                )
            }
        }
    }

    suspend fun imageCache(key: String, ignoreCache: Boolean = false, block: suspend () -> ByteArray): ByteArray {
        if (!ignoreCache) {
            val cached = database.imageCacheQueries.getByKey(key).executeAsOneOrNull()
            if (cached != null) return cached.data_
        } else {
            database.imageCacheQueries.remove(key)
        }

        val new = block()
        database.imageCacheQueries.insert(key, new)
        return new
    }

    fun insertOrUpdateAdminTicket(order: ProductOrder) {
        val element = database.adminTicketsQueries
            .getById(order.id)
            .executeAsOneOrNull()
        with(order) {
            if (element == null) {
                // insert
                database.adminTicketsQueries.insert(
                    id,
                    lastUpdate,
                    eventId,
                    orderNumber,
                    customerId,
                    customerName,
                    hasBeenValidated,
                    _cache_meta_data
                )
            } else {
                // update
                database.adminTicketsQueries.update(
                    eventId,
                    lastUpdate,
                    orderNumber,
                    customerId,
                    customerName,
                    hasBeenValidated,
                    _cache_meta_data,
                    id
                )
            }
        }
    }

    /**
     * Updates the cached value of whether the ticket for the given order has been validated, as well as updating its
     * last update time.
     */
    fun updateIsValidated(orderId: Long, isValidated: Boolean) {
        database.adminTicketsQueries.updateIsValidated(isValidated, Clock.System.now().toEpochMilliseconds(), orderId)
    }
}
