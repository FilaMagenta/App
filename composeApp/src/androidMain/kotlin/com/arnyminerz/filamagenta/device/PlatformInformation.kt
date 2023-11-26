package com.arnyminerz.filamagenta.device

actual object PlatformInformation {
    /**
     * Returns the platform that the current device is currently running.
     */
    actual fun currentPlatform(): Platform = Platform.ANDROID

    /**
     * Should be updated by the Application when launching and stores whether the device has a
     * camera.
     */
    var hasCameraFeature: Boolean = false

    /**
     * Checks whether the device has a camera.
     */
    actual fun isCameraSupported(): Boolean = hasCameraFeature

    /**
     * Should be updated by the Application when launching and stores whether the device has NFC support.
     */
    var hasNfcFeature: Boolean = false

    /**
     * Checks whether the device supports NFC.
     */
    actual fun isNfcSupported(): Boolean = hasNfcFeature

    /**
     * Should be updated by the Application when launching and stores whether the device has BLE support.
     */
    var hasBleFeature: Boolean = false

    /**
     * Checks whether the device supports BLE.
     */
    actual fun isBLESupported(): Boolean = hasBleFeature
}
