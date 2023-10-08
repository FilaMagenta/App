package me.gilo.woodroid.repo.product

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.ShippingClassAPI
import me.gilo.woodroid.models.ShippingClass
import me.gilo.woodroid.models.filters.ShippingClassesFilter
import me.gilo.woodroid.repo.WooRepository

class ShippingClassRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: ShippingClassAPI = ktorfit.create()

    fun create(shippingClass: ShippingClass): Call<ShippingClass> {
        return apiService.create(shippingClass)
    }


    fun shippingClass(id: Int): Call<ShippingClass> {
        return apiService.view(id)
    }

    fun shippingClasses(): Call<List<ShippingClass>> {
        return apiService.list()
    }

    fun shippingClasses(shippingClassesFilter: ShippingClassesFilter): Call<List<ShippingClass>> {
        return apiService.filter(shippingClassesFilter.filters)
    }

    fun update(id: Int, shippingClass: ShippingClass): Call<ShippingClass> {
        return apiService.update(id, shippingClass)
    }

    fun delete(id: Int): Call<ShippingClass> {
        return apiService.delete(id)
    }

    fun delete(id: Int, force: Boolean): Call<ShippingClass> {
        return apiService.delete(id, force)
    }


}
