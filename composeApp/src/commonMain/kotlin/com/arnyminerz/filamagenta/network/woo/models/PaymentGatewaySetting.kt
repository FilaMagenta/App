package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.Serializable

@Serializable
class PaymentGatewaySetting {

    lateinit var id: String
    lateinit var label: String
    lateinit var description: String
    lateinit var type: String
    lateinit var value: String
    lateinit var default_value: String
    lateinit var tip: String
    lateinit var placeholder: String
}
