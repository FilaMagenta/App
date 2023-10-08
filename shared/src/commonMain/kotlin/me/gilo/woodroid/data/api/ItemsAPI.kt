package me.gilo.woodroid.data.api


import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import me.gilo.cocart.model.CartItem
import me.gilo.cocart.model.CartTotal
import me.gilo.woodroid.data.requests.CartItemRequest
import me.gilo.woodroid.data.requests.CartRequest

interface ItemsAPI {

    @Headers("Content-Type: application/json")
    @POST("add-item")
    fun addToCart(@Body body: CartItemRequest): Call<CartItem>

    @Headers("Content-Type: application/json")
    @GET("get-cart")
    fun list(@Query("thumb") thumb: Boolean = true): Call<Map<String, CartItem>>

    @POST("get-cart/saved")
    fun getCustomerCart(@Body body: CartRequest): Call<Map<String, CartItem>>

    @Headers("Content-Type: application/json")
    @POST("clear")
    fun clear(): Call<String>

    @Headers("Content-Type: application/json")
    @GET("count-items")
    fun count(): Call<Int>

    @Headers("Content-Type: application/json")
    @POST("calculate")
    fun calculate(@Query("return") returnTotal: Boolean = true): Call<String>

    @Headers("Content-Type: application/json")
    @GET("totals")
    fun totals(@Query("html") returnTotal: Boolean = true): Call<CartTotal>

}