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

interface OrderNoteAPI {

    @Headers("Content-Type: application/json")
    @POST("orders/{id}/notes")
    fun create(@Path("id") order_id: Int, @Body body: com.arnyminerz.filamagenta.network.woo.models.OrderNote): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote>

    @GET("orders/{id}/notes/{note_id}")
    fun view(@Path("id") order_id: Int, @Path("note_id") note_id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote>

    @GET("orders/{id}/notes")
    fun list(@Path("id") order_id: Int): Call<List<com.arnyminerz.filamagenta.network.woo.models.OrderNote>>

    @DELETE("orders/{id}/notes/{note_id}")
    fun delete(@Path("id") order_id: Int, @Path("note_id") note_id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote>

    @DELETE("orders/{id}/notes/{note_id}")
    fun delete(@Path("id") order_id: Int, @Path("note_id") note_id: Int, @Query("force") force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote>

    @GET("orders/{id}/notes")
    fun filter(@Path("id") order_id: Int, @QueryMap filter: Map<String, String>): Call<List<com.arnyminerz.filamagenta.network.woo.models.OrderNote>>

}