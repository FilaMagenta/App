package com.arnyminerz.filamagenta.ui.browser

import com.arnyminerz.filamagenta.device.PlatformInformation

object BrowserManager {
    suspend fun launchUrl(url: String) {
        if (PlatformInformation.hasSpecificBrowserUi()) {
            CustomBrowserUi.launchUri(url)
        } else {
            InAppWebBrowserStateHandler.launch(url)
        }
    }
}
