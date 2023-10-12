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
import me.gilo.woodroid.models.Variation

interface ProductVariationAPI {

    @Headers("Content-Type: application/json")
    @POST("products/{id}/variations")
    fun create(@Path("id") product_id: Int, @Body body: Variation): Call<Variation>

    @GET("products/{id}/variations/{variation_id}")
    fun view(@Path("id") product_id: Int, @Path("variation_id") variation_id: Int): Call<Variation>

    @GET("products/{id}/variations")
    fun list(@Path("id") product_id: Int): Call<List<Variation>>

    @Headers("Content-Type: application/json")
    @PUT("products/{id}/variations/{variation_id}")
    fun update(@Path("id") product_id: Int, @Path("variation_id") variation_id: Int, @Body body: Variation): Call<Variation>

    @DELETE("products/{id}/variations/{variation_id}")
    fun delete(@Path("id") product_id: Int, @Path("variation_id") variation_id: Int): Call<Variation>

    @DELETE("products/{id}/variations/{variation_id}")
    fun delete(@Path("id") product_id: Int, @Path("variation_id") variation_id: Int, @Query("force") force: Boolean): Call<Variation>

    @Headers("Content-Type: application/json")
    @PUT("products/{id}/variations/{variation_id}")
    fun batch(@Path("id") product_id: Int, @Path("variation_id") variation_id: Int, @Body body: com.arnyminerz.filamagenta.network.woo.models.Product): Call<Variation>

    @GET("products/{id}/variations")
    fun filter(@Path("id") product_id: Int, @QueryMap filter: Map<String, String>): Call<List<Variation>>

}