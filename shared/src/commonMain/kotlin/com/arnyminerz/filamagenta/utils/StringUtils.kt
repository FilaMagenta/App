package com.arnyminerz.filamagenta.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

fun LocalDateTime.toEpochMillisecondsString(timeZone: TimeZone = TimeZone.UTC): String =
    toInstant(timeZone).toEpochMilliseconds().toString()
