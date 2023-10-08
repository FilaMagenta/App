package me.gilo.woodroid.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class Webhook {
    var id: Int = 0
    lateinit var name: String
    lateinit var status: String
    lateinit var topic: String
    lateinit var resource: String
    lateinit var event: String
    lateinit var hooks: Array<String>
    lateinit var delivery_url: String
    lateinit var secret: String
    lateinit var date_created: LocalDateTime
    lateinit var date_created_gmt: LocalDateTime
    lateinit var date_modified: LocalDateTime
    lateinit var date_modified_gmt: LocalDateTime
}
