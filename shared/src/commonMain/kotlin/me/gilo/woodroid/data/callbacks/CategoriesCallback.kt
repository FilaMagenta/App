package me.gilo.woodroid.data.callbacks

import kotlinx.serialization.SerialName


class CategoriesCallback {
    @SerialName("product_categories")
    lateinit var categories: ArrayList<com.arnyminerz.filamagenta.network.woo.models.Category>
}
