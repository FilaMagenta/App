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
import me.gilo.woodroid.models.TaxRate

interface TaxRateAPI {

    @Headers("Content-Type: application/json")
    @POST("taxes")
    fun create(@Body body: TaxRate): Call<TaxRate>

    @GET("taxes/{id}")
    fun view(@Path("id") id: Int): Call<TaxRate>

    @GET("taxes")
    fun list(): Call<List<TaxRate>>

    @Headers("Content-Type: application/json")
    @PUT("taxes/{id}")
    fun update(@Path("id") id: Int, @Body body: TaxRate): Call<TaxRate>

    @DELETE("taxes/{id}")
    fun delete(@Path("id") id: Int): Call<TaxRate>

    @DELETE("taxes/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<TaxRate>

    @POST("taxes/batch")
    fun batch(@Body body: TaxRate): Call<String>

    @GET("coupons")
    fun filter(@QueryMap filter: Map<String, String>): Call<ArrayList<TaxRate>>

}