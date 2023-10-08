package me.gilo.woodroid.data.api

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import me.gilo.woodroid.models.ShippingMethod

interface ShippingMethodAPI {

    @GET("shipping_methods/{id}")
    fun view(@Path("id") id: String): Call<ShippingMethod>

    @GET("shipping_methods")
    fun list(): Call<List<ShippingMethod>>

}