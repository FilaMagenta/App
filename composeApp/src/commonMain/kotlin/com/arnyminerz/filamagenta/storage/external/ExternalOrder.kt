package com.arnyminerz.filamagenta.storage.external

data class ExternalOrder(
    val name: String,
    val order: String,
    val phone: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ExternalOrder

        if (name != other.name) return false
        if (order != other.order) return false
        if (phone != other.phone) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + order.hashCode()
        result = 31 * result + phone.hashCode()
        return result
    }
}
