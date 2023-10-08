package com.arnyminerz.filamagenta.storage

import com.russhwolf.settings.Settings

/**
 * Should be implemented by each platform. Returns the factory to be used in the current platform.
 */
var settingsFactory: Settings.Factory? = null

expect class SettingsFactoryProvider {
    val factory: Settings.Factory
}
