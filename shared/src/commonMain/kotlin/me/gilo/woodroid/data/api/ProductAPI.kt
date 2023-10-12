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

interface ProductAPI {


    @GET("products")
    fun getProducts(): Call<ArrayList<com.arnyminerz.filamagenta.network.woo.models.Product>>

    @GET("products/count")
    fun getProductsCount(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Product>>

    @GET("products/{id}")
    fun getProduct(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Product>

    @GET("products")
    fun getProducts(@Query("filter[category]") category: String): Call<ArrayList<com.arnyminerz.filamagenta.network.woo.models.Product>>

    @GET("products")
    fun search(@Query("search") search: String): Call<ArrayList<com.arnyminerz.filamagenta.network.woo.models.Product>>

    @GET("products")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<com.arnyminerz.filamagenta.network.woo.models.Product>>


    @Headers("Content-Type: application/json")
    @POST("products")
    fun create(@Body body: com.arnyminerz.filamagenta.network.woo.models.Product): Call<com.arnyminerz.filamagenta.network.woo.models.Product>

    @GET("products/{id}")
    fun view(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Product>

    @GET("products")
    fun list(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Product>>

    @Headers("Content-Type: application/json")
    @PUT("products/{id}")
    fun update(@Path("id") id: Int, @Body body: com.arnyminerz.filamagenta.network.woo.models.Product): Call<com.arnyminerz.filamagenta.network.woo.models.Product>

    @DELETE("products/{id}")
    fun delete(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Product>

    @DELETE("products/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.Product>

    @POST("products/batch")
    fun batch(@Body body: com.arnyminerz.filamagenta.network.woo.models.Product): Call<String>

}