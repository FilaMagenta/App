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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.network.ktorfit.get
import com.arnyminerz.filamagenta.network.woo.wooCommerce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import me.gilo.woodroid.models.Product

@Composable
fun EventsPage(isAdmin: Boolean) {
    var products by remember { mutableStateOf<List<Product>?>(null) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            println("Getting products from server...")
            wooCommerce.ProductRepository().products().get().also { products = it }
            println("Got ${products?.size} products from server.")
        }
    }

    AnimatedContent(
        targetState = products,
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
                items(list) { item ->
                    val date = item.meta_data.find { it.key == "event_date" }
                    if (date != null || isAdmin) {
                        Text(
                            text = item.name
                        )
                    }
                }
            }
        }
    }
}
