package com.arnyminerz.filamagenta.device

import com.arnyminerz.filamagenta.ui.browser.CustomBrowserUi

actual object PlatformInformation {
    /**
     * Returns the platform that the current device is currently running.
     */
    actual fun currentPlatform(): Platform = Platform.IOS

    /**
     * Should return `true` if the current platform has a specific a more integrated approach of displaying runtime
     * browser.
     *
     * If `true` is returned, [CustomBrowserUi.launchUri] should be implemented.
     */
    actual fun hasSpecificBrowserUi(): Boolean = false
}
