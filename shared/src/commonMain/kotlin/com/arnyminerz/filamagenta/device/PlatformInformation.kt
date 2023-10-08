package com.arnyminerz.filamagenta.device

import com.arnyminerz.filamagenta.network.CustomBrowserUi

expect object PlatformInformation {
    /**
     * Returns the platform that the current device is currently running.
     */
    fun currentPlatform(): Platform

    /**
     * Should return `true` if the current platform has a specific a more integrated approach of displaying runtime
     * browser.
     *
     * If `true` is returned, [CustomBrowserUi.launchUri] should be implemented.
     */
    fun hasSpecificBrowserUi(): Boolean
}
