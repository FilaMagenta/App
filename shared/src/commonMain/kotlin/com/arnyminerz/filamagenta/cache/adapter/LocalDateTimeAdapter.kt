package com.arnyminerz.filamagenta.cache.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object LocalDateTimeAdapter: ColumnAdapter<LocalDateTime, Long> {
    override fun decode(databaseValue: Long): LocalDateTime {
        return Instant.fromEpochMilliseconds(databaseValue).toLocalDateTime(TimeZone.UTC)
    }

    override fun encode(value: LocalDateTime): Long {
        return value.toInstant(TimeZone.UTC).toEpochMilliseconds()
    }
}
