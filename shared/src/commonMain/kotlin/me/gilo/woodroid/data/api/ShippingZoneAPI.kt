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
import me.gilo.woodroid.models.ShippingZone

interface ShippingZoneAPI {

    @Headers("Content-Type: application/json")
    @POST("shipping/zones")
    fun create(@Body body: ShippingZone): Call<ShippingZone>

    @GET("shipping/zones/{id}")
    fun view(@Path("id") id: Int): Call<ShippingZone>

    @GET("shipping/zones")
    fun list(): Call<List<ShippingZone>>

    @Headers("Content-Type: application/json")
    @PUT("shipping/zones/{id}")
    fun update(@Path("id") id: Int, @Body body: ShippingZone): Call<ShippingZone>

    @DELETE("shipping/zones/{id}")
    fun delete(@Path("id") id: Int): Call<ShippingZone>

    @DELETE("shipping/zones/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<ShippingZone>

    @POST("shipping/zones/batch")
    fun batch(@Body body: ShippingZone): Call<String>

    @GET("coupons")
    fun filter(@QueryMap filter: Map<String, String>): Call<ArrayList<ShippingZone>>

}