package com.arnyminerz.filamagenta.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.network.ktorfit.get
import com.arnyminerz.filamagenta.network.woo.wooCommerce
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
            println("Getting products from server...")
            wooCommerce.ProductRepository().products().get().also { products ->
                println("Got ${products.size} products from server.")
                for (product in products) {
                    val date = product.meta_data
                        .find { it.key == "event_date" }
                        ?.value
                        ?.toLong()
                        ?.let(Instant::fromEpochMilliseconds)
                        ?.toLocalDateTime(TimeZone.currentSystemDefault())

                    Cache.insertOrUpdate(
                        Event(product.id, product.name, date)
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
                items(list) { event ->
                    val date = event.date
                    if (date != null || isAdmin) {
                        Text(
                            text = event.name
                        )
                    }
                }
            }
        }
    }
}
