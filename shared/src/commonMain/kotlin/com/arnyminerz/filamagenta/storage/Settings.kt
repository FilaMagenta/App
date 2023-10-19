package com.arnyminerz.filamagenta.storage

import com.russhwolf.settings.ObservableSettings

/**
 * The general configuration storage.
 */
val settings by lazy { settingsFactory.create("settings") as ObservableSettings }

object SettingsKeys {
    /**
     * Stores the language selected by the user for the UI.
     */
    const val LANGUAGE = "lang"

    /**
     * Whether the user wants their data to be collected for research.
     */
    const val DATA_COLLECTION = "data_collection"

    /**
     * Stores whether the admin information screen has been shown to the user.
     */
    const val SYS_SHOWN_ADMIN = "_shown_admin"

    /**
     * Stores whether the scanner information has already been shown to the user.
     */
    const val SYS_SHOWN_SCANNER = "_shown_scanner"

    /**
     * Used for recovering the state of an event being visualized when the app restarts, for example, after scanning an
     * NFC tag.
     */
    const val SYS_VIEWING_EVENT = "_viewing_event"
}
