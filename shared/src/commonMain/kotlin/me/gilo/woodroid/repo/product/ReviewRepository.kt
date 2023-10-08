package me.gilo.woodroid.repo.product

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.ProductReviewAPI
import me.gilo.woodroid.models.ProductReview
import me.gilo.woodroid.models.filters.ProductReviewFilter
import me.gilo.woodroid.repo.WooRepository

class ReviewRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: ProductReviewAPI = ktorfit.create()

    fun create(review: ProductReview): Call<ProductReview> {
        return apiService.create(review)
    }


    fun review(id: Int): Call<ProductReview> {
        return apiService.view(id)
    }

    fun reviews(): Call<List<ProductReview>> {
        return apiService.list()
    }

    fun reviews(productReviewFilter: ProductReviewFilter): Call<List<ProductReview>> {
        return apiService.filter(productReviewFilter.filters)
    }

    fun update(id: Int, review: ProductReview): Call<ProductReview> {
        return apiService.update(id, review)
    }

    fun delete(id: Int): Call<ProductReview> {
        return apiService.delete(id)
    }

    fun delete(id: Int, force: Boolean): Call<ProductReview> {
        return apiService.delete(id, force)
    }


}
