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
import me.gilo.woodroid.models.TaxClass

interface TaxClassAPI {

    @Headers("Content-Type: application/json")
    @POST("taxes/classes")
    fun create(@Body body: TaxClass): Call<TaxClass>

    @GET("taxes/classes")
    fun list(): Call<List<TaxClass>>

    @DELETE("taxes/classes/{id}")
    fun delete(@Path("id") id: Int): Call<TaxClass>

    @DELETE("taxes/classes/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<TaxClass>

    @GET("coupons")
    fun filter(@QueryMap filter: Map<String, String>): Call<ArrayList<TaxClass>>

}