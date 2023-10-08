package com.arnyminerz.filamagenta.storage

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings

actual class SettingsFactoryProvider {
    actual val factory: Settings.Factory = NSUserDefaultsSettings.Factory()
}
