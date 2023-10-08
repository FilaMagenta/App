package me.gilo.woodroid.models

import kotlinx.serialization.Serializable

@Serializable
class TaxClass {
    lateinit var slug: String
    lateinit var name: String
}
