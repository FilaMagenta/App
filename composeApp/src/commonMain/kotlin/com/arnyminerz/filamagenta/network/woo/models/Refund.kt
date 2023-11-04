package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class Refund {

    var refunded_by: String? = null
    var reason: String? = null
    var amount: String? = null
    var date_created: String? = null
    var meta_data: Array<com.arnyminerz.filamagenta.network.woo.models.Metadata>? = null
    var date_created_gmt: String? = null
    var id: String? = null
    var line_items: Array<com.arnyminerz.filamagenta.network.woo.models.LineItem>? = null
    var refunded_payment: String? = null
}



