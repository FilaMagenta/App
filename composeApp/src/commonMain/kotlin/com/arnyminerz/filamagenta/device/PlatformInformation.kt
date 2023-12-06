package com.arnyminerz.filamagenta.device

expect object PlatformInformation {
    /**
     * Returns the platform that the current device is currently running.
     */
    fun currentPlatform(): Platform

    /**
     * Checks whether the device has a camera.
     */
    fun isCameraSupported(): Boolean

    /**
     * Checks whether the device supports NFC.
     */
    fun isNfcSupported(): Boolean

    /**
     * Whether the device supports processing Excel files.
     */
    fun isExcelSupported(): Boolean
}
