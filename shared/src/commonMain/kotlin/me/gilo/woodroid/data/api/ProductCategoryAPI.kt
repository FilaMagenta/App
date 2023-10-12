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

interface ProductCategoryAPI {

    @Headers("Content-Type: application/json")
    @POST("products/categories")
    fun create(@Body body: com.arnyminerz.filamagenta.network.woo.models.Category): Call<com.arnyminerz.filamagenta.network.woo.models.Category>

    @GET("products/categories/{id}")
    fun view(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Category>

    @GET("products/categories")
    fun list(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Category>>

    @Headers("Content-Type: application/json")
    @PUT("products/categories/{id}")
    fun update(@Path("id") id: Int, @Body body: com.arnyminerz.filamagenta.network.woo.models.Category): Call<com.arnyminerz.filamagenta.network.woo.models.Category>

    @DELETE("products/categories/{id}")
    fun delete(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Category>

    @DELETE("products/categories/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.Category>

    @POST("products/categories/batch")
    fun batch(@Body body: com.arnyminerz.filamagenta.network.woo.models.Category): Call<String>

    @GET("products/categories")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<com.arnyminerz.filamagenta.network.woo.models.Category>>

}