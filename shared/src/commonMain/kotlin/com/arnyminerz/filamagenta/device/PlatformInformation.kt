package com.arnyminerz.filamagenta.device

expect object PlatformInformation {
    /**
     * Returns the platform that the current device is currently running.
     */
    fun currentPlatform(): Platform
}
