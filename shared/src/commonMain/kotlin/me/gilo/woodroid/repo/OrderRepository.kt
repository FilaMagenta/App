package me.gilo.woodroid.repo

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.OrderAPI
import me.gilo.woodroid.models.filters.OrderFilter
import me.gilo.woodroid.repo.order.OrderNoteRepository
import me.gilo.woodroid.repo.order.RefundRepository

class OrderRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: OrderAPI = ktorfit.create()

    internal var orderNoteRepository: OrderNoteRepository
    internal var refundRepository: RefundRepository

    init {

        orderNoteRepository = OrderNoteRepository(baseUrl, consumerKey, consumerSecret)
        refundRepository = RefundRepository(baseUrl, consumerKey, consumerSecret)
    }

    fun create(order: com.arnyminerz.filamagenta.network.woo.models.Order): Call<com.arnyminerz.filamagenta.network.woo.models.Order> {
        return apiService.create(order)
    }

    fun addToCart(productId: Int, cartOrder: com.arnyminerz.filamagenta.network.woo.models.Order?): Call<com.arnyminerz.filamagenta.network.woo.models.Order> {
        var cartOrder = cartOrder
        val lineItem = com.arnyminerz.filamagenta.network.woo.models.LineItem()
        lineItem.productId = productId
        lineItem.quantity = 1

        if (cartOrder != null) {
            cartOrder.addLineItem(lineItem)
            return apiService.update(cartOrder.id, cartOrder)
        } else {
            cartOrder = com.arnyminerz.filamagenta.network.woo.models.Order()
            cartOrder.orderNumber = "Cart"
            cartOrder.status = "on-hold"
            cartOrder.addLineItem(lineItem)
            return apiService.create(cartOrder)
        }

    }

    fun cart(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Order>> {
        val orderFilter = OrderFilter()
        orderFilter.status = "on-hold"

        return apiService.filter(orderFilter.filters)
    }

    fun order(id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Order> {
        return apiService.view(id)
    }

    fun orders(): Call<List<com.arnyminerz.filamagenta.network.woo.models.Order>> {
        return apiService.list()
    }

    fun orders(orderFilter: OrderFilter): Call<List<com.arnyminerz.filamagenta.network.woo.models.Order>> {
        return apiService.filter(orderFilter.filters)
    }

    fun update(id: Int, order: com.arnyminerz.filamagenta.network.woo.models.Order): Call<com.arnyminerz.filamagenta.network.woo.models.Order> {
        return apiService.update(id, order)
    }

    fun delete(id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.Order> {
        return apiService.delete(id)
    }

    fun delete(id: Int, force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.Order> {
        return apiService.delete(id, force)
    }


    fun createNote(order: com.arnyminerz.filamagenta.network.woo.models.Order, note: com.arnyminerz.filamagenta.network.woo.models.OrderNote): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote> {
        return orderNoteRepository.create(order, note)
    }

    fun note(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote> {
        return orderNoteRepository.note(order, id)
    }

    fun notes(order: com.arnyminerz.filamagenta.network.woo.models.Order): Call<List<com.arnyminerz.filamagenta.network.woo.models.OrderNote>> {
        return orderNoteRepository.notes(order)
    }

    fun deleteNote(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote> {
        return orderNoteRepository.delete(order, id)
    }

    fun deleteNote(order: com.arnyminerz.filamagenta.network.woo.models.Order, id: Int, force: Boolean): Call<com.arnyminerz.filamagenta.network.woo.models.OrderNote> {
        return orderNoteRepository.delete(order, id, force)
    }


}
