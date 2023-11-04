package com.arnyminerz.filamagenta.cache.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

object LocalDateAdapter: ColumnAdapter<LocalDate, Long> {
    override fun decode(databaseValue: Long): LocalDate {
        return Instant.fromEpochMilliseconds(databaseValue).toLocalDateTime(TimeZone.UTC).date
    }

    override fun encode(value: LocalDate): Long {
        return value.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }
}
