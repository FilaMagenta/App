package com.arnyminerz.filamagenta.ui.platform

import androidx.compose.runtime.Composable

object PlatformSettings {
    const val SECTION_ACCOUNT = "account"

    const val SECTION_UI = "ui"

    const val SECTION_INFO = "info"
}

/**
 * If the platform requires any special settings section, it can be defined here.
 * The key of the map corresponds with one of the sections defined in [PlatformSettings] named `SECTION_*`.
 */
expect val platformSpecificSettings: Map<String, @Composable () -> Unit>
