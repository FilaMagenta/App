package com.arnyminerz.filamagenta.device

actual object PlatformInformation {
    /**
     * Returns the platform that the current device is currently running.
     */
    actual fun currentPlatform(): Platform = Platform.IOS

    /**
     * Checks whether the device has a camera.
     */
    actual fun isCameraSupported(): Boolean = true

    /**
     * Checks whether the device supports NFC.
     */
    actual fun isNfcSupported(): Boolean = false
}
