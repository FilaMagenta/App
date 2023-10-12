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

interface CouponAPI {

    @Headers("Content-Type: application/json")
    @POST("coupons")
    fun create(@Body body: com.arnyminerz.filamagenta.network.woo.models.Coupon): Call<com.arnyminerz.filamagenta.network.woo.models.Coupon>

    @GET("coupons/{id}")
    fun view(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Coupon>

    @GET("coupons")
    fun list(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Coupon>>

    @GET("coupons")
    fun filter(@QueryMap filter: Map<String, String>): Call<List<com.arnyminerz.filamagenta.network.woo.models.Coupon>>

    @Headers("Content-Type: application/json")
    @PUT("coupons/{id}")
    fun update(@Path("id") id: Int, @Body body: com.arnyminerz.filamagenta.network.woo.models.Coupon): Call<com.arnyminerz.filamagenta.network.woo.models.Coupon>

    @DELETE("coupons/{id}")
    fun delete(@Path("id") id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Coupon>

    @DELETE("coupons/{id}")
    fun delete(@Path("id") id: Int, @Query("force") force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.Coupon>

    @POST("coupons/batch")
    fun batch(@Body body: com.arnyminerz.filamagenta.network.woo.models.Coupon): Call<String>

}