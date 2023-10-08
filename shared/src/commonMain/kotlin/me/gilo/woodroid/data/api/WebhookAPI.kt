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
import me.gilo.woodroid.models.Webhook
import me.gilo.woodroid.models.WebhookDelivery

interface WebhookAPI {

    @Headers("Content-Type: application/json")
    @POST("webhooks")
    fun create(@Body body: Webhook): Call<Webhook>

    @GET("webhooks")
    fun list(): Call<List<Webhook>>

    @GET("webhooks/{id}")
    fun view(@Path("id") id: Int): Call<Webhook>

    @GET("webhooks/{id}/deliveries/{delivery_id}")
    fun delivery(@Path("id") webhook_id: Int, @Path("delivery_id") delivery_id: Int): Call<WebhookDelivery>

    @GET("webhooks/{id}/deliveries")
    fun deliveries(@Path("id") webhook_id: Int): Call<List<WebhookDelivery>>

    @Headers("Content-Type: application/json")
    @PUT("webhooks/{id}")
    fun update(@Path("id") id: Int, @Body body: Webhook): Call<Webhook>

    @DELETE("webhooks/{id}")
    fun delete(@Path("id") id: Int): Call<Webhook>

    @DELETE("webhooks/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<Webhook>

    @GET("webhooks")
    fun filter(@QueryMap filter: Map<String, String>): Call<ArrayList<Webhook>>

}