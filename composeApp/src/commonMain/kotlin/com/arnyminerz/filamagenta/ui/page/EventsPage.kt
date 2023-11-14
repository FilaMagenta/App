package com.arnyminerz.filamagenta.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.isComplete
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.list.EventItem
import com.arnyminerz.filamagenta.ui.reusable.LoadingBox
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until

/**
 * Every how many hours should the events list be refreshed automatically.
 */
private const val SyncEventsEveryHours = 12

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventsPage(viewModel: MainViewModel) {
    val isAdmin by viewModel.isAdmin.collectAsState(false)

    val events by Cache.events.collectAsState(null)

    DisposableEffect(events) {
        val lastSync = settings.getLongOrNull(SettingsKeys.SYS_EVENTS_LAST_SYNC)?.let(Instant::fromEpochMilliseconds)
        val now = Clock.System.now()

        val coroutine = if (
            // Sync if there aren't any events
            events?.isEmpty() == true ||
            // Or lastSync is null
            // Or time since last sync is greater than SyncWalletEveryHours
            lastSync?.let { it.until(now, DateTimeUnit.HOUR) >= SyncEventsEveryHours } != false
        ) {
            viewModel.refreshEvents()
        } else {
            null
        }

        onDispose {
            runBlocking { coroutine?.cancelAndJoin() }
        }
    }

    events
        // Display incomplete events only to admins
        ?.filter { event -> isAdmin == true || event.isComplete }
        ?.let { list ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 350.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = list,
                    key = { it.id }
                ) { event ->
                    EventItem(
                        event = event,
                        modifier = Modifier.animateItemPlacement()
                    ) { viewModel.viewEvent(event) }
                }
            }
        } ?: LoadingBox()
}
