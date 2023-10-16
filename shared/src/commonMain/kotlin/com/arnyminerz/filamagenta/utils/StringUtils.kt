package com.arnyminerz.filamagenta.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

fun LocalDateTime.toEpochMillisecondsString(timeZone: TimeZone = TimeZone.UTC): String =
    toInstant(timeZone).toEpochMilliseconds().toString()

/**
 * The letters used for validating DNIs.
 */
private const val DniValidationLetters = "TRWAGMYFPDXBNJZSQVHLCKE"

/**
 * The length that a DNI is supposed to have.
 */
private const val DniLength = 9

/**
 * Checks whether `this` String is a valid DNI following the rules published by the
 * [Ministerio de Interior](https://www.interior.gob.es/opencms/ca/servicios-al-ciudadano/tramites-y-gestiones/dni/calculo-del-digito-de-control-del-nif-nie/)
 * of Spain.
 *
 * Example of valid DNI: `123456789X`
 */
val String.isValidDni: Boolean
    get() = if (length != DniLength) {
        false
    } else {
        val letter = get(DniLength - 1)
        val number = substring(0, DniLength - 1).toInt()
        val mod = number % DniValidationLetters.length
        DniValidationLetters[mod] == letter
    }
