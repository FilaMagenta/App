package com.arnyminerz.filamagenta.ui.platform

import androidx.compose.runtime.Composable

/**
 * If the platform requires any special settings section, it can be defined here.
 * The key of the map corresponds with one of the sections defined in [PlatformSettings] named `SECTION_*`.
 */
actual val platformSpecificSettings: Map<String, @Composable () -> Unit> = emptyMap()
