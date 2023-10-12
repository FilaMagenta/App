package com.arnyminerz.filamagenta.network.woo.models.filters

class WebhookFilter : ListFilter() {

    internal lateinit var status: String

    fun getStatus(): String {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
        addFilter("status", status)
    }
}
