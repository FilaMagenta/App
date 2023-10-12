package me.gilo.woodroid.repo.order

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.RefundAPI
import me.gilo.woodroid.models.Refund
import me.gilo.woodroid.models.filters.RefundFilter
import me.gilo.woodroid.repo.WooRepository

class RefundRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: RefundAPI = ktorfit.create()

    fun create(order: com.arnyminerz.filamagenta.network.woo.models.Order, refund: Refund): Call<Refund> {
        return apiService.create(order.id, refund)
    }

    fun refund(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int): Call<Refund> {
        return apiService.view(order.id, id)
    }

    fun refunds(order: com.arnyminerz.filamagenta.network.woo.models.Order): Call<List<Refund>> {
        return apiService.list(order.id)
    }

    fun refunds(order: com.arnyminerz.filamagenta.network.woo.models.Order, refundFilter: RefundFilter): Call<List<Refund>> {
        return apiService.filter(order.id, refundFilter.filters)
    }

    fun delete(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int): Call<Refund> {
        return apiService.delete(order.id, id)
    }

    fun delete(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int, force: Boolean): Call<Refund> {
        return apiService.delete(order.id, id, force)
    }


}
