package com.arnyminerz.filamagenta.ui.browser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

object InAppWebBrowserStateHandler {
    val url = MutableStateFlow<String?>(null)

    val shouldDisplay = url.map { it != null }

    suspend fun launch(url: String) {
        withContext(Dispatchers.Main) {
            this@InAppWebBrowserStateHandler.url.value = url
        }
    }
}
