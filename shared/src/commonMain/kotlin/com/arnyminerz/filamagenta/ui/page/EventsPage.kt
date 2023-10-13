package com.arnyminerz.filamagenta.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.isComplete
import com.arnyminerz.filamagenta.cache.data.toEvent
import com.arnyminerz.filamagenta.network.woo.WooCommerce
import com.arnyminerz.filamagenta.ui.list.EventItem
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun EventsPage(isAdmin: Boolean, onEventRequested: (Event) -> Unit) {
    val events by Cache.events.collectAsState(null)
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            isRefreshing = true

            Napier.d("Getting products from server...")
            WooCommerce.Products.getProducts().also { products ->
                Napier.i("Got ${products.size} products from server. Updating cache...")
                for (product in products) {
                    Cache.insertOrUpdate(
                        product.toEvent()
                    )
                }
            }
        }.invokeOnCompletion { isRefreshing = false }
    }

    AnimatedContent(
        targetState = events,
        modifier = Modifier.fillMaxSize()
    ) { list ->
        if (list == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (isRefreshing) {
                    stickyHeader {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateEnterExit(
                                    enter = slideInVertically { -it },
                                    exit = slideOutVertically { -it }
                                )
                        )
                    }
                }

                items(
                    items = list
                        // Just display events without a date to admins
                        .filter { event -> isAdmin || event.isComplete },
                    key = { it.id }
                ) { event ->
                    EventItem(
                        event,
                        modifier = Modifier.animateItemPlacement()
                    ) { onEventRequested(event) }
                }
            }
        }
    }
}
