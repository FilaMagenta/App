package me.gilo.woodroid.repo

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.PaymentGatewayAPI

class PaymentGatewayRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: PaymentGatewayAPI = ktorfit.create()

    fun paymentGateway(id: Int): Call<com.arnyminerz.filamagenta.network.woo.models.PaymentGateway> {
        return apiService.view(id)
    }

    fun paymentGateways(): Call<List<com.arnyminerz.filamagenta.network.woo.models.PaymentGateway>> {
        return apiService.list()
    }

    fun update(id: String, paymentGateway: com.arnyminerz.filamagenta.network.woo.models.PaymentGateway): Call<com.arnyminerz.filamagenta.network.woo.models.PaymentGateway> {
        return apiService.update(id, paymentGateway)
    }

}
