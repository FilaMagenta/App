package com.arnyminerz.filamagenta.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.isComplete
import com.arnyminerz.filamagenta.network.ktorfit.await
import com.arnyminerz.filamagenta.network.woo.wooCommerce
import com.arnyminerz.filamagenta.ui.list.EventItem
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun EventsPage(isAdmin: Boolean) {
    val events by Cache.events.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            Napier.d("Getting products from server...")
            wooCommerce.ProductRepository().products().await().also { products ->
                Napier.i("Got ${products.size} products from server. Updating cache...")
                for (product in products) {
                    val date = product.meta_data
                        .find { it.key == "event_date" }
                        ?.value
                        ?.toLong()
                        ?.let(Instant::fromEpochMilliseconds)
                        ?.toLocalDateTime(TimeZone.currentSystemDefault())
                    val type = product.meta_data
                        .find { it.key == "category" }
                        ?.value
                        ?.let(EventType::valueOf)

                    Cache.insertOrUpdate(
                        Event(product.id, product.name, date, type)
                    )
                }
            }
        }
    }

    AnimatedContent(
        targetState = events,
        modifier = Modifier.fillMaxSize()
    ) { list ->
        if (list.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = list
                        // Just display events without a date to admins
                        .filter { event -> isAdmin || event.isComplete },
                    key = { it.id }
                ) { event ->
                    EventItem(event)
                }
            }
        }
    }
}
