package com.arnyminerz.filamagenta.cache

import com.arnyminerz.filamagenta.cache.adapter.LocalDateTimeAdapter

lateinit var database: Database
    private set

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(
        driver = driver,
        EventAdapter = Event.Adapter(
            dateAdapter = LocalDateTimeAdapter
        )
    ).also { database = it }
}
