package com.arnyminerz.filamagenta.network.woo.update

import com.arnyminerz.filamagenta.network.woo.models.Metadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetadataUpdate(
    @SerialName("meta_data") val metadata: List<Metadata>
): WooProductUpdate
