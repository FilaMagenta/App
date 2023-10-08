package me.gilo.woodroid.repo

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.SettingsAPI
import me.gilo.woodroid.models.SettingGroup
import me.gilo.woodroid.models.SettingOption

class SettingsRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: SettingsAPI = ktorfit.create()

    fun settings(): Call<List<SettingGroup>> {
        return apiService.settings()
    }

    fun option(group_id: String, option_id: String): Call<SettingOption> {
        return apiService.option(group_id, option_id)
    }

    fun options(group_id: String): Call<List<SettingOption>> {
        return apiService.options(group_id)
    }

    fun updateOption(group_id: String, option_id: String, option: SettingOption): Call<SettingOption> {
        return apiService.update(group_id, option_id, option)
    }

}
