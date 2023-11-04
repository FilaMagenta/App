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
import com.arnyminerz.filamagenta.ui.list.EventItem
import com.arnyminerz.filamagenta.ui.reusable.LoadingBox
import com.arnyminerz.filamagenta.ui.state.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventsPage(viewModel: MainViewModel) {
    val isAdmin by viewModel.isAdmin.collectAsState(false)

    val events by Cache.events.collectAsState(null)

    DisposableEffect(events) {
        val coroutine = if (events?.isEmpty() == true) {
            viewModel.refreshEvents()
        } else {
            null
        }

        onDispose { coroutine?.cancel() }
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
