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
import me.gilo.woodroid.models.Order

interface OrderAPI {

    @Headers("Content-Type: application/json")
    @POST("orders")
    fun create(@Body body: Order): Call<Order>

    @GET("orders/{id}")
    fun view(@Path("id") id: Int): Call<Order>

    @GET("orders")
    fun list(): Call<List<Order>>

    @Headers("Content-Type: application/json")
    @PUT("orders/{id}")
    fun update(@Path("id") id: Int, @Body body: Order): Call<Order>

    @DELETE("orders/{id}")
    fun delete(@Path("id") id: Int): Call<Order>

    @DELETE("orders/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<Order>

    @POST("orders/batch")
    fun batch(@Body body: Order): Call<String>

    @GET("orders")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<Order>>

}