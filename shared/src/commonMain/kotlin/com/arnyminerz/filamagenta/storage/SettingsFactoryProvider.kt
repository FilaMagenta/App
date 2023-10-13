package com.arnyminerz.filamagenta.storage

import com.russhwolf.settings.Settings

/**
 * Should be implemented by each platform. Returns the factory to be used in the current platform.
 */
lateinit var settingsFactory: Settings.Factory

expect class SettingsFactoryProvider {
    val factory: Settings.Factory
}
