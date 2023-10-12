package me.gilo.woodroid.repo.product

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.ProductCategoryAPI
import me.gilo.woodroid.models.filters.ProductCategoryFilter
import me.gilo.woodroid.repo.WooRepository

class CategoryRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: ProductCategoryAPI = ktorfit.create()

    fun create(category: com.arnyminerz.filamagenta.network.woo.models.Category): Call<com.arnyminerz.filamagenta.network.woo.models.Category> {
        return apiService.create(category)
    }


    fun category(id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Category> {
        return apiService.view(id)
    }

    fun categories(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Category>> {
        return apiService.list()
    }

    fun categories(productCategoryFilter: ProductCategoryFilter): Call<List<com.arnyminerz.filamagenta.network.woo.models.Category>> {
        return apiService.filter(productCategoryFilter.filters)
    }

    fun update(id: Int, category: com.arnyminerz.filamagenta.network.woo.models.Category): Call<com.arnyminerz.filamagenta.network.woo.models.Category> {
        return apiService.update(id, category)
    }

    fun delete(id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Category> {
        return apiService.delete(id)
    }

    fun delete(id: Int, force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.Category> {
        return apiService.delete(id, force)
    }


}
