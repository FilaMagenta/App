package com.arnyminerz.filamagenta.network.woo.utils

import com.arnyminerz.filamagenta.network.woo.models.Metadata
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Searches for a [Metadata] in the list with [Metadata.key] equal to [key], and converts it into a [LocalDateTime].
 *
 * Considers the data to be a long, which then is converted into an [Instant] with [Instant.fromEpochMilliseconds].
 *
 * @param key The key to search for.
 *
 * @return The converted [LocalDateTime], or `null` if [key] was not found.
 */
fun List<Metadata>.getDateTime(key: String): LocalDateTime? = this
    .find { it.key == key }
    ?.value
    ?.toLong()
    ?.let(Instant::fromEpochMilliseconds)
    ?.toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * Retrieves an enum value from a list of metadata based on the provided key.
 *
 * @param key the key to search for in the list of metadata
 * @param valueOf a lambda function that takes a name string and returns an enum value of type E, or null if the name
 * does not match any of the enum constants.
 *
 * @return the matching enum value, or null if the key is not found or the name does not match any of the enum constants
 */
fun <E: Enum<E>> List<Metadata>.getEnum(key: String, valueOf: (name: String) -> E?): E? = this
    .find { it.key == key }
    ?.value
    ?.let(valueOf)
