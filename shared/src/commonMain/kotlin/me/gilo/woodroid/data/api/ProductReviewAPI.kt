package me.gilo.woodroid.data.api


import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import me.gilo.woodroid.models.ProductReview

interface ProductReviewAPI {

    @Headers("Content-Type: application/json")
    @POST("products/reviews")
    fun create(@Body body: ProductReview): Call<ProductReview>

    @GET("products/reviews/{id}")
    fun view(@Path("id") id: Int): Call<ProductReview>

    @GET("products/reviews")
    fun list(): Call<List<ProductReview>>

    @Headers("Content-Type: application/json")
    @PUT("products/reviews/{id}")
    fun update(@Path("id") id: Int, @Body body: ProductReview): Call<ProductReview>

    @DELETE("products/reviews/{id}")
    fun delete(@Path("id") id: Int): Call<ProductReview>

    @DELETE("products/reviews/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<ProductReview>

    @POST("products/reviews/batch")
    fun batch(@Body body: ProductReview): Call<String>

    @GET("products/reviews")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<ProductReview>>

}