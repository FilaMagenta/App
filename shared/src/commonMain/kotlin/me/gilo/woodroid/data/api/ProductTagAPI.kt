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
import me.gilo.woodroid.models.Tag

interface ProductTagAPI {

    @Headers("Content-Type: application/json")
    @POST("products/tags")
    fun create(@Body body: Tag): Call<Tag>

    @GET("products/tags/{id}")
    fun view(@Path("id") id: Int): Call<Tag>

    @GET("products/tags")
    fun list(): Call<List<Tag>>

    @Headers("Content-Type: application/json")
    @PUT("products/tags/{id}")
    fun update(@Path("id") id: Int, @Body body: Tag): Call<Tag>

    @DELETE("products/tags/{id}")
    fun delete(@Path("id") id: Int): Call<Tag>

    @DELETE("products/tags/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<Tag>

    @POST("products/tags/batch")
    fun batch(@Body body: Tag): Call<String>

    @GET("products/tags")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<Tag>>

}