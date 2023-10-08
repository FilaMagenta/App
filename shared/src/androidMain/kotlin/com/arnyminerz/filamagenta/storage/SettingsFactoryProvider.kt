package com.arnyminerz.filamagenta.storage

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual class SettingsFactoryProvider(context: Context) {
    actual val factory: Settings.Factory = SharedPreferencesSettings.Factory(context)
}
