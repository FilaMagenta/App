package com.arnyminerz.filamagenta.lifecycle

import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.storage.settingsFactory
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking

// TODO: Call those from iOS

/**
 * Initializes Napier logging and Libsodium.
 */
fun initialize() = runBlocking {
    // Initialize logging library
    Napier.base(DebugAntilog())

    // Initialize libsodium
    LibsodiumInitializer.initialize()
}

/**
 * Updates the currently selected locale by extracting it from settings.
 *
 * **Note: [settingsFactory] must be initialized**
 */
fun updateLocale() {
    // Set the locale to display
    StringDesc.localeType = settings.getStringOrNull(SettingsKeys.LANGUAGE)
        ?.let { StringDesc.LocaleType.Custom(it) }
        ?: StringDesc.LocaleType.System
}
