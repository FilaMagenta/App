package me.gilo.woodroid.data.api

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import me.gilo.woodroid.models.PaymentGateway

interface PaymentGatewayAPI {


    @GET("payment_gateways/{id}")
    fun view(@Path("id") id: Int): Call<PaymentGateway>

    @GET("payment_gateways")
    fun list(): Call<List<PaymentGateway>>

    @Headers("Content-Type: application/json")
    @PUT("payment_gateways/{id}")
    fun update(@Path("id") id: String, @Body body: PaymentGateway): Call<PaymentGateway>

}