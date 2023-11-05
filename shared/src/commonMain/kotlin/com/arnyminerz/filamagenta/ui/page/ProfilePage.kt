package com.arnyminerz.filamagenta.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.qrcode
import com.arnyminerz.filamagenta.image.QRCodeGenerator
import com.arnyminerz.filamagenta.ui.modifier.placeholder.placeholder
import com.arnyminerz.filamagenta.ui.native.toImageBitmap
import com.arnyminerz.filamagenta.ui.reusable.ImageLoader
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@OptIn(ExperimentalEncodingApi::class, ExperimentalUnsignedTypes::class)
@Composable
fun ProfilePage(viewModel: MainViewModel) {
    val viewModelAccount by viewModel.account.collectAsState()

    val accountFullName by viewModel.accountFullName.collectAsState()

    viewModelAccount?.let { account ->
        val qr = account.qrcode()
        var qrCode by remember { mutableStateOf<ByteArray?>(null) }

        LaunchedEffect(account) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = qr.encrypt()
                qrCode = Cache.imageCache(data) {
                    QRCodeGenerator.generate(data)
                }
            }
        }

        DisposableEffect(account) {
            val coroutine = if (accountFullName == null) {
                viewModel.refreshAccount()
            } else {
                null
            }

            onDispose { coroutine?.cancel() }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageLoader(
                image = qrCode?.toImageBitmap(),
                contentDescription = account.name,
                modifier = Modifier
                    .size(192.dp)
                    .padding(top = 32.dp)
            )
            Text(
                text = accountFullName ?: "0123456789",
                modifier = Modifier
                    .placeholder(
                        visible = accountFullName == null
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    lineHeight = MaterialTheme.typography.titleMedium.fontSize
                ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    }
}