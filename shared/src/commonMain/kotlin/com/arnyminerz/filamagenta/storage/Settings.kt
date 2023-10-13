package com.arnyminerz.filamagenta.storage

/**
 * The general configuration storage.
 */
val settings by lazy { settingsFactory.create("settings") }

object SettingsKeys {
    const val LANGUAGE = "lang"
}
