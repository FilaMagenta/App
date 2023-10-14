package com.arnyminerz.filamagenta.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.isComplete
import com.arnyminerz.filamagenta.ui.list.EventItem
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

    events?.let { list ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = list
                    // Just display events without a date to admins
                    .filter { event -> isAdmin == true || event.isComplete },
                key = { it.id }
            ) { event ->
                EventItem(
                    event,
                    modifier = Modifier.animateItemPlacement()
                ) { viewModel.viewEvent(event) }
            }
        }
    } ?: Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) { CircularProgressIndicator() }
}
