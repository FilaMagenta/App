package com.arnyminerz.filamagenta.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

object Cache {
    val events: Flow<List<Event>> =
        database.eventQueries
            .getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)

    val transactions: Flow<List<AccountTransaction>> =
        database.accountTransactionQueries
            .getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)

    fun insertOrUpdate(event: Event) {
        val element = database.eventQueries
            .getById(event.id)
            .executeAsOneOrNull()
        if (element == null) {
            // insert
            database.eventQueries.insert(event.id, event.name)
        } else {
            // update
            database.eventQueries.update(event.name, event.id)
        }
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
}
