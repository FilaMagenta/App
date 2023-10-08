package com.arnyminerz.filamagenta.ui.browser

actual object CustomBrowserUi {
    /**
     * Launches the given URI in the platform-specific browser. Should throw [UnsupportedOperationException] if not
     * supported by current platform.
     *
     * Check with [PlatformInformation.hasSpecificBrowserUi]
     */
    actual fun launchUri(uri: String) {
        throw UnsupportedOperationException()
    }
}