package com.arnyminerz.filamagenta.device

actual object PlatformInformation {
    /**
     * Returns the platform that the current device is currently running.
     */
    actual fun currentPlatform(): Platform = Platform.IOS

}
