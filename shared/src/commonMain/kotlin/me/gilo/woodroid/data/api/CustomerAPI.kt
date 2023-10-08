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
import me.gilo.woodroid.models.Customer
import me.gilo.woodroid.models.Download

interface CustomerAPI {

    @Headers("Content-Type: application/json")
    @POST("customers")
    fun create(@Body body: Customer): Call<Customer>

    @GET("customers/{id}")
    fun view(@Path("id") id: Int): Call<Customer>

    @GET("customers")
    fun list(): Call<List<Customer>>

    @Headers("Content-Type: application/json")
    @PUT("customers/{id}")
    fun update(@Path("id") id: Int, @Body body: Customer): Call<Customer>

    @DELETE("customers/{id}")
    fun delete(@Path("id") id: Int): Call<Customer>

    @DELETE("customers/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<Customer>

    @POST("customers/batch")
    fun batch(@Body body: Customer): Call<String>

    @POST("customers/{id}/downloads")
    fun downloads(@Path("id") id: Int): Call<List<Download>>

    @GET("customers")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<Customer>>

}