package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventVariation.Companion.toEventVariations
import com.arnyminerz.filamagenta.network.woo.models.Metadata
import com.arnyminerz.filamagenta.network.woo.models.Product
import com.arnyminerz.filamagenta.network.woo.models.Variation
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
 * Checks whether the event type has ticket support.
 *
 * Supported ones: [EventType.Breakfast], [EventType.Lunch] and [EventType.Dinner].
 */
val Event.hasTicket: Boolean
    get() = sequenceOf(EventType.Breakfast, EventType.Lunch, EventType.Dinner).contains(type)

/**
 * Extracts all the Metadata cached in [Event] to be used with [Product].
 */
fun Event.extractMetadata(): List<Metadata> {
    return DefaultJson.decodeFromString(_cache_meta_data)
}

/**
 * Converts the [Product] into a cacheable [Event].
 */
fun Product.toEvent(variations: List<Variation>): Event {
    val date = metaData.getDateTime(ProductMeta.EVENT_DATE)
    val type = metaData.getEnum(ProductMeta.CATEGORY, EventType::valueOf)

    return Event(id, name, date, type, variations.toEventVariations(), DefaultJson.encodeToString(metaData))
}
