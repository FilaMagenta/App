package me.gilo.woodroid.data.api


import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import me.gilo.woodroid.models.SettingGroup
import me.gilo.woodroid.models.SettingOption

interface SettingsAPI {

    @GET("settings")
    fun settings(): Call<List<SettingGroup>>

    @GET("settings/{group_id}/{id}")
    fun option(@Path("group_id") group_id: String, @Path("id") option_id: String): Call<SettingOption>

    @GET("settings/{id}")
    fun options(@Path("id") group_id: String): Call<List<SettingOption>>

    @Headers("Content-Type: application/json")
    @PUT("settings/{group_id}/{id}")
    fun update(
        @Path("group_id") group_id: String,
        @Path("id") option_id: String,
        @Body body: SettingOption
    ): Call<SettingOption>


}