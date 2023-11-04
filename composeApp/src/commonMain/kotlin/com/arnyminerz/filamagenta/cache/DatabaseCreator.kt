package com.arnyminerz.filamagenta.cache

import app.cash.sqldelight.EnumColumnAdapter
import com.arnyminerz.filamagenta.cache.adapter.LocalDateAdapter
import com.arnyminerz.filamagenta.cache.adapter.LocalDateTimeAdapter
import com.arnyminerz.filamagenta.cache.data.EventVariation.EventVariationsColumnAdapter

lateinit var database: Database
    private set

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    return Database(
        driver = driver,
        EventAdapter = Event.Adapter(
            dateAdapter = LocalDateTimeAdapter,
            typeAdapter = EnumColumnAdapter(),
            variationsAdapter = EventVariationsColumnAdapter
        ),
        AccountTransactionAdapter = AccountTransaction.Adapter(
            dateAdapter = LocalDateAdapter
        ),
        ProductOrderAdapter = ProductOrder.Adapter(
            dateAdapter = LocalDateTimeAdapter
        )
    ).also { database = it }
}
