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
     * Stores the currently selected account name.
     */
    const val SELECTED_ACCOUNT = "account"

    /**
     * Stores whether the admin information screen has been shown to the user.
     */
    const val SYS_SHOWN_ADMIN = "_shown_admin"

    /**
     * Used for recovering the state of an event being visualized when the app restarts, for example, after scanning an
     * NFC tag.
     */
    const val SYS_VIEWING_EVENT = "_viewing_event"

    /**
     * The time of the last wallet synchronization performed.
     */
    const val SYS_WALLET_LAST_SYNC = "_last_wallet_sync"

    /**
     * The time of the last events synchronization performed.
     */
    const val SYS_EVENTS_LAST_SYNC = "_last_events_sync"

    /**
     * Only applies for Android. The instant in epoch millis when the sync worker was last run.
     */
    const val SYS_WORKER_LAST_SYNC = "_last_worker_sync"
}
