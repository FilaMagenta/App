package me.gilo.woodroid.utils

import kotlinx.datetime.LocalDateTime

object Converter {
    fun getDateString(date: LocalDateTime): String {
        return "yyyy-MM-ddTHH:mm:ss.XXXXXX"
            .replace("yyyy", date.year.toString().padStart(4, '0'))
            .replace("MM", date.monthNumber.toString().padStart(2, '0'))
            .replace("dd", date.dayOfMonth.toString().padStart(2, '0'))
            .replace("HH", date.hour.toString().padStart(2, '0'))
            .replace("mm", date.minute.toString().padStart(2, '0'))
            .replace("ss", date.second.toString().padStart(2, '0'))
            .replace("SSS", date.nanosecond.toString().padStart(6, '0'))
    }
}
