package me.gilo.cocart.repo

import com.arnyminerz.filamagenta.network.httpClient
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Ktorfit
import me.gilo.cocart.model.CartItem
import me.gilo.woodroid.data.api.ItemsAPI
import me.gilo.woodroid.data.requests.CartItemRequest
import me.gilo.woodroid.data.requests.CartRequest

class CoCartRepository(private var baseUrl: String, consumerKey: String, consumerSecret: String) {

    private var apiService: ItemsAPI
    private var ktorfit: Ktorfit

    init {
        ktorfit = Ktorfit.Builder()
            .baseUrl(baseUrl)
            .httpClient(httpClient)
            .build()

        apiService = ktorfit.create()
    }

    fun addToCart(productId: Int, quantity: Int): Call<CartItem> {
        val cartItemRequest = CartItemRequest(
            productId = productId, quantity = quantity
        )

        return apiService.addToCart(cartItemRequest)
    }

    fun cart(): Call<Map<String, CartItem>> {
        return apiService.list()
    }

    fun cart(customerId: Int, thumb:Boolean = false): Call<Map<String, CartItem>> {
        return apiService.list(thumb)
    }

    fun getCustomerCart(customerId: String, thumb:Boolean = false): Call<Map<String, CartItem>> {
        return apiService.getCustomerCart(CartRequest(customerId=customerId, thumb = true))
    }


}
