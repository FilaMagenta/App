package com.arnyminerz.filamagenta.device

import android.content.Context
import androidx.browser.customtabs.CustomTabsClient
import com.arnyminerz.filamagenta.ui.browser.CustomBrowserUi
import java.util.Collections

actual object PlatformInformation {
    private var customTabsSupported: Boolean = false

    /**
     * Checks whether the device has a web browser that supports custom tabs, and stores the result of the operation.
     * [hasSpecificBrowserUi] will then respond in consequence.
     */
    fun checkIfCustomTabsSupported(context: Context) {
        val pn = CustomTabsClient.getPackageName(context, Collections.emptyList())
        customTabsSupported = pn != null
    }

    /**
     * Returns the platform that the current device is currently running.
     */
    actual fun currentPlatform(): Platform = Platform.ANDROID

    /**
     * Should return `true` if the current platform has a specific a more integrated approach of displaying runtime
     * browser.
     *
     * If `true` is returned, [CustomBrowserUi.launchUri] should be implemented.
     */
    actual fun hasSpecificBrowserUi(): Boolean = customTabsSupported
}
