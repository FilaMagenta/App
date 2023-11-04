package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class TaxLine {
    var id: Int = 0
    var rate_id: Int = 0
    var code: String? = null
    var title: String? = null
    var total: Double = 0.toDouble()
    var isCompound: Boolean = false
}
