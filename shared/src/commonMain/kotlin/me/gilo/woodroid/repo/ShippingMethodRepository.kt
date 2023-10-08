package me.gilo.woodroid.repo

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.ShippingMethodAPI
import me.gilo.woodroid.models.ShippingMethod

class ShippingMethodRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: ShippingMethodAPI = ktorfit.create()

    fun shippingMethod(id: String): Call<ShippingMethod> {
        return apiService.view(id)
    }

    fun shippingMethods(): Call<List<ShippingMethod>> {
        return apiService.list()
    }


}
