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
import me.gilo.woodroid.models.ShippingClass

interface ShippingClassAPI {

    @Headers("Content-Type: application/json")
    @POST("products/shipping_classes")
    fun create(@Body body: ShippingClass): Call<ShippingClass>

    @GET("products/shipping_classes/{id}")
    fun view(@Path("id") id: Int): Call<ShippingClass>

    @GET("products/shipping_classes")
    fun list(): Call<List<ShippingClass>>

    @Headers("Content-Type: application/json")
    @PUT("products/shipping_classes/{id}")
    fun update(@Path("id") id: Int, @Body body: ShippingClass): Call<ShippingClass>

    @DELETE("products/shipping_classes/{id}")
    fun delete(@Path("id") id: Int): Call<ShippingClass>

    @DELETE("products/shipping_classes/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<ShippingClass>

    @POST("products/shipping_classes/batch")
    fun batch(@Body body: ShippingClass): Call<String>

    @GET("products/shipping_classes")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<ShippingClass>>

}