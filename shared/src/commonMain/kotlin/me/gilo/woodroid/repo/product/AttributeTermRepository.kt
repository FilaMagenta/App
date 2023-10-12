package me.gilo.woodroid.repo.product

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.ProductAttributeTermAPI
import me.gilo.woodroid.models.filters.ProductAttributeFilter
import me.gilo.woodroid.repo.WooRepository

class AttributeTermRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: ProductAttributeTermAPI = ktorfit.create()

    fun create(attribute_id: Int, term: com.arnyminerz.filamagenta.network.woo.models.AttributeTerm): Call<com.arnyminerz.filamagenta.network.woo.models.AttributeTerm> {
        return apiService.create(attribute_id, term)
    }

    fun term(attribute_id: Int, id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.AttributeTerm> {
        return apiService.view(attribute_id, id)
    }

    fun terms(attribute_id: Int): Call<List<com.arnyminerz.filamagenta.network.woo.models.AttributeTerm>> {
        return apiService.list(attribute_id)
    }

    fun terms(attribute_id: Int, productAttributeFilter: ProductAttributeFilter): Call<List<com.arnyminerz.filamagenta.network.woo.models.AttributeTerm>> {
        return apiService.filter(attribute_id, productAttributeFilter.filters)
    }

    fun update(attribute_id: Int, id: Int, term: com.arnyminerz.filamagenta.network.woo.models.AttributeTerm): Call<com.arnyminerz.filamagenta.network.woo.models.AttributeTerm> {
        return apiService.update(attribute_id, id, term)
    }

    fun delete(attribute_id: Int, id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.AttributeTerm> {
        return apiService.delete(attribute_id, id)
    }

    fun delete(attribute_id: Int, id: Int, force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.AttributeTerm> {
        return apiService.delete(attribute_id, id, force)
    }


}
