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

interface OrderAPI {

    @Headers("Content-Type: application/json")
    @POST("orders")
    fun create(@Body body: com.arnyminerz.filamagenta.network.woo.models.Order): Call<com.arnyminerz.filamagenta.network.woo.models.Order>

    @GET("orders/{id}")
    fun view(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Order>

    @GET("orders")
    fun list(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Order>>

    @Headers("Content-Type: application/json")
    @PUT("orders/{id}")
    fun update(@Path("id") id: Int, @Body body: com.arnyminerz.filamagenta.network.woo.models.Order): Call<com.arnyminerz.filamagenta.network.woo.models.Order>

    @DELETE("orders/{id}")
    fun delete(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Order>

    @DELETE("orders/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.Order>

    @POST("orders/batch")
    fun batch(@Body body: com.arnyminerz.filamagenta.network.woo.models.Order): Call<String>

    @GET("orders")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<com.arnyminerz.filamagenta.network.woo.models.Order>>

}