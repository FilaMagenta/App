package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.network.woo.models.Metadata
import com.arnyminerz.filamagenta.network.woo.models.Product
import com.arnyminerz.filamagenta.network.woo.utils.ProductMeta
import com.arnyminerz.filamagenta.network.woo.utils.getDateTime
import com.arnyminerz.filamagenta.network.woo.utils.getEnum
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString

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

/**
 * Extracts all the Metadata cached in [Event] to be used with [Product].
 */
fun Event.extractMetadata(): List<Metadata> {
    return DefaultJson.decodeFromString(_cache_meta_data)
}

/**
 * Converts the [Product] into a cacheable [Event].
 */
fun Product.toEvent(): Event {
    val date = meta_data.getDateTime(ProductMeta.EVENT_DATE)
    val type = meta_data.getEnum(ProductMeta.CATEGORY, EventType::valueOf)

    return Event(id, name, date, type, DefaultJson.encodeToString(meta_data))
}
