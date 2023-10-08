package me.gilo.woodroid.data.api


import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import me.gilo.woodroid.models.Refund

interface RefundAPI {

    @Headers("Content-Type: application/json")
    @POST("orders/{id}/refunds")
    fun create(@Path("id") order_id: Int, @Body body: Refund): Call<Refund>

    @GET("orders/{id}/refunds/{refund_id}")
    fun view(@Path("id") order_id: Int, @Path("refund_id") refund_id: Int): Call<Refund>

    @GET("orders/{id}/refunds")
    fun list(@Path("id") order_id: Int): Call<List<Refund>>

    @DELETE("orders/{id}/refunds/{refund_id}")
    fun delete(@Path("id") order_id: Int, @Path("refund_id") refund_id: Int): Call<Refund>

    @DELETE("orders/{id}/refunds/{refund_id}")
    fun delete(@Path("id") order_id: Int, @Path("refund_id") refund_id: Int, @Query("force") force: Boolean): Call<Refund>

    @GET("orders/{id}/refunds")
    fun filter(@Path("id") order_id: Int, @QueryMap filter: Map<String, String>): Call<List<Refund>>

}