package me.gilo.woodroid.repo

import de.jensklingenberg.ktorfit.Call
import me.gilo.woodroid.data.api.PaymentGatewayAPI
import me.gilo.woodroid.models.PaymentGateway

class PaymentGatewayRepository(baseUrl: String, consumerKey: String, consumerSecret: String) :
    WooRepository(baseUrl, consumerKey, consumerSecret) {

    private val apiService: PaymentGatewayAPI = ktorfit.create()

    fun paymentGateway(id: Int): Call<PaymentGateway> {
        return apiService.view(id)
    }

    fun paymentGateways(): Call<List<PaymentGateway>> {
        return apiService.list()
    }

    fun update(id: String, paymentGateway: PaymentGateway): Call<PaymentGateway> {
        return apiService.update(id, paymentGateway)
    }

}
