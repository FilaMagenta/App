package com.arnyminerz.filamagenta.cache.data

import app.cash.sqldelight.ColumnAdapter
import com.arnyminerz.filamagenta.network.woo.models.Variation
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class EventVariation(
    val name: String,
    val description: String? = null,
    val options: List<String>
) {
    companion object {
        fun List<Variation>.toEventVariations(): List<EventVariation> {
            // First, remove all variations without attributes
            val byName = filterNot { it.attributes.isNullOrEmpty() }
                // And group by their attribute ids
                .groupBy { it.attributes!!.first().id }
            // Now, we should have all the attributes grouped; those are the actual EventVariations.
            return byName.mapNotNull { (id, variations) ->
                // The first element is taken as reference
                val base = variations.first()
                // And used for the description
                val description = base.description
                // All entries will have the same name, so take the first one. If it's null, the variation is ignored
                val name = base.attributes?.firstOrNull()?.name
                if (name != null) {
                    // Only take variations with a name
                    EventVariation(
                        name,
                        description,
                        variations.flatMap { variation ->
                            // Flatten map even though there should be only one attribute.
                            variation.attributes!!.filter { it.id == id }.mapNotNull { it.option }
                        }
                    )
                } else {
                    null
                }
            }
        }
    }

    object EventVariationsColumnAdapter : ColumnAdapter<List<EventVariation>, String> {
        override fun decode(databaseValue: String): List<EventVariation> {
            return DefaultJson.decodeFromString(databaseValue)
        }

        override fun encode(value: List<EventVariation>): String {
            return DefaultJson.encodeToString(value)
        }
    }
}
