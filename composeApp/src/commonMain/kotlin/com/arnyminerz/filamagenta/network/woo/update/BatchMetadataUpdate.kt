package com.arnyminerz.filamagenta.network.woo.update

import com.arnyminerz.filamagenta.network.woo.models.Metadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class BatchMetadataUpdate(
    val update: List<Entry>
): WooOrderUpdate {
    @Serializable
    data class Entry(
        val id: Long,
        @SerialName("meta_data")
        val metadata: List<Metadata>
    )
}
