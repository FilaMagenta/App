package me.gilo.woodroid.data.callbacks

import kotlinx.serialization.SerialName
import me.gilo.woodroid.models.Store

class StoreCallback {

    @SerialName("store")
    lateinit var store: Store
}
