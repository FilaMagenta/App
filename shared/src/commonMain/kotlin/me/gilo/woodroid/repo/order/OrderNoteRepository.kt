package me.gilo.woodroid.repo.order

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.OrderNoteAPI
import me.gilo.woodroid.models.filters.OrderNoteFilter
import me.gilo.woodroid.repo.WooRepository

class OrderNoteRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: OrderNoteAPI = ktorfit.create()

    fun create(order: com.arnyminerz.filamagenta.network.woo.models.Order, note: com.arnyminerz.filamagenta.network.woo.models.OrderNote): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote> {
        return apiService.create(order.id, note)
    }

    fun note(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote> {
        return apiService.view(order.id, id)
    }

    fun notes(order: com.arnyminerz.filamagenta.network.woo.models.Order): Call<List<com.arnyminerz.filamagenta.network.woo.models.OrderNote>> {
        return apiService.list(order.id)
    }

    fun notes(order: com.arnyminerz.filamagenta.network.woo.models.Order, orderNoteFilter: OrderNoteFilter): Call<List<com.arnyminerz.filamagenta.network.woo.models.OrderNote>> {
        return apiService.filter(order.id, orderNoteFilter.filters)
    }

    fun delete(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote> {
        return apiService.delete(order.id, id)
    }

    fun delete(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int, force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote> {
        return apiService.delete(order.id, id, force)
    }


}
