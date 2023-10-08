package me.gilo.woodroid.data.callbacks

import kotlinx.serialization.SerialName
import me.gilo.woodroid.models.Category


class CategoriesCallback {
    @SerialName("product_categories")
    lateinit var categories: ArrayList<Category>
}
