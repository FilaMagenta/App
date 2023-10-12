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

interface CustomerAPI {

    @Headers("Content-Type: application/json")
    @POST("customers")
    fun create(@Body body: com.arnyminerz.filamagenta.network.woo.models.Customer): Call<com.arnyminerz.filamagenta.network.woo.models.Customer>

    @GET("customers/{id}")
    fun view(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Customer>

    @GET("customers")
    fun list(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Customer>>

    @Headers("Content-Type: application/json")
    @PUT("customers/{id}")
    fun update(@Path("id") id: Int, @Body body: com.arnyminerz.filamagenta.network.woo.models.Customer): Call<com.arnyminerz.filamagenta.network.woo.models.Customer>

    @DELETE("customers/{id}")
    fun delete(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Customer>

    @DELETE("customers/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.Customer>

    @POST("customers/batch")
    fun batch(@Body body: com.arnyminerz.filamagenta.network.woo.models.Customer): Call<String>

    @POST("customers/{id}/downloads")
    fun downloads(@Path("id") id: Int): Call<List<com.arnyminerz.filamagenta.network.woo.models.Download>>

    @GET("customers")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<com.arnyminerz.filamagenta.network.woo.models.Customer>>

}