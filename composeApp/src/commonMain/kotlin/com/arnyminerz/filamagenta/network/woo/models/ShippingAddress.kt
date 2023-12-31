package com.arnyminerz.filamagenta.network.woo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ShippingAddress {
    var id: Int = 0
    @SerialName("first_name")
    lateinit var firstName: String
    @SerialName("last_name")
    lateinit var lastName: String
    lateinit var company: String
    @SerialName("address_1")
    lateinit var address1: String
    @SerialName("address_2")
    lateinit var address2: String
    lateinit var city: String
    lateinit var state: String
    lateinit var postcode: String
    lateinit var country: String

    override fun toString(): String {
        return (firstName + " " + lastName + "\n" +
                address1 + " " + address2 + "\n"
                + city + ", " + state + " " + postcode + "\n"
                + country)
    }
}
