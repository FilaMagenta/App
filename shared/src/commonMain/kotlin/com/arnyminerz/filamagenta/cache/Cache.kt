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
}
