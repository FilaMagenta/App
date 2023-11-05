package com.arnyminerz.filamagenta.ui.platform

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.getLongState
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.reusable.settings.SettingsItem
import com.arnyminerz.filamagenta.worker.SyncWorker
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until

/**
 * If the platform requires any special settings section, it can be defined here.
 * The key of the map corresponds with one of the sections defined in [PlatformSettings] named `SECTION_*`.
 */
actual val platformSpecificSettings: Map<String, @Composable () -> Unit> = mapOf(
    PlatformSettings.SECTION_INFO to {
        val state by SyncWorker.getWorkLiveData(LocalContext.current).observeAsState()
        val lastSyncState by settings.getLongState(key = SettingsKeys.SYS_WORKER_LAST_SYNC, defaultValue = -1)
        val lastSync = lastSyncState.takeIf { it >= 0 }?.let(Instant::fromEpochMilliseconds)

        SettingsItem(
            headline = stringResource(MR.strings.settings_worker_title),
            summary = stringResource(
                MR.strings.settings_worker_summary,
                SyncWorker.INTERVAL_HOURS,
                if (state == null) {
                    stringResource(MR.strings.settings_worker_summary_not_scheduled)
                } else if (lastSync == null) {
                    stringResource(MR.strings.settings_worker_summary_never)
                } else {
                    stringResource(MR.strings.settings_worker_summary_last, lastSync.until(Clock.System.now(), DateTimeUnit.HOUR))
                }
            ),
            icon = Icons.Rounded.CloudSync
        )
    }
)
