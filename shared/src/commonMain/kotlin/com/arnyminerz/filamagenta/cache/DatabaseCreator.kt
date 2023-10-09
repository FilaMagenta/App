package com.arnyminerz.filamagenta.cache

import app.cash.sqldelight.EnumColumnAdapter
import com.arnyminerz.filamagenta.cache.adapter.LocalDateTimeAdapter

lateinit var database: Database
    private set

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(
        driver = driver,
        EventAdapter = Event.Adapter(
            dateAdapter = LocalDateTimeAdapter,
            typeAdapter = EnumColumnAdapter()
        )
    ).also { database = it }
}
