package me.gilo.woodroid.data.api

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface FiltersAPI {

    @Headers("Content-Type: application/json")
    @POST("clear")
    fun clear(): Call<String>

    @GET("count-items")
    fun count(): Call<Int>


}