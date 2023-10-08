package me.gilo.woodroid.data.callbacks


import kotlinx.serialization.SerialName
import me.gilo.woodroid.models.ProductReview


class ReviewsCallback {
    @SerialName("product_reviews")
    lateinit var productReviews: ArrayList<ProductReview>
}
