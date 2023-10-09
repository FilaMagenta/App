package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.Event

private val cleanNameCapExceptions = listOf("y", "i", "de", "del", "la", "les", "las", "el", "els", "los")

val Event.cleanName: String
    get() {
        var result = name
        // Remove all leading numbers
        while (result[0].isDigit()) result = result.substring(1)
        // Trim leading and trailing spaces and dots
        result = result.trim(' ', '.')
        // Turn everything to lowercase
        result = result.lowercase()
        // Capitalize all words except cleanNameCapExceptions
        result = result.split(' ')
            .joinToString(" ") { word ->
                if (cleanNameCapExceptions.find { it.equals(word, true) } != null) {
                    // If word is in exceptions, return as is
                    word
                } else {
                    // Otherwise capitalize
                    word.replaceFirstChar { it.uppercaseChar() }
                }
            }
        return result
    }

/**
 * Returns whether the event is complete for the user to see. This means that all the required metadata is complete.
 * Metadata required:
 * - [Event.date]
 * - [Event.type]
 */
val Event.isComplete: Boolean
    get() = date != null && type != null
