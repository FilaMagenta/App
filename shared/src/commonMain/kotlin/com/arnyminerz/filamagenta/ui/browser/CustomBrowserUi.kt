package com.arnyminerz.filamagenta.ui.browser

expect object CustomBrowserUi {
    /**
     * Launches the given URI in the platform-specific browser. Should throw [UnsupportedOperationException] if not
     * supported by current platform.
     *
     * Check with [PlatformInformation.hasSpecificBrowserUi]
     */
    fun launchUri(uri: String)
}