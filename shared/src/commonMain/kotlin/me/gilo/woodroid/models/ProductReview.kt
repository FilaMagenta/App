package me.gilo.woodroid.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
class ProductReview {
    var id: Int = 0
    var date_created: LocalDateTime? = null
    var date_created_gmt: LocalDateTime? = null
    var product_id: Int = 0
    lateinit var reviewer: String
    lateinit var reviewer_email: String

    lateinit var reviewer_avatar_urls: Map<String, String>

    var review: String? = null
    var rating: Int = 0
    var isVerified: Boolean = false
}
