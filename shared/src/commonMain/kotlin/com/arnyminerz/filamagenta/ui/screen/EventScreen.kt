package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Cache.collectListAsState
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.cleanName
import com.arnyminerz.filamagenta.cache.data.hasTicket
import com.arnyminerz.filamagenta.cache.data.qrcode
import com.arnyminerz.filamagenta.device.PlatformInformation
import com.arnyminerz.filamagenta.image.QRCodeGenerator
import com.arnyminerz.filamagenta.ui.dialog.UsersModalBottomSheet
import com.arnyminerz.filamagenta.ui.native.toImageBitmap
import com.arnyminerz.filamagenta.ui.reusable.EventInformationRow
import com.arnyminerz.filamagenta.ui.reusable.ImageLoader
import com.arnyminerz.filamagenta.ui.reusable.LoadingCard
import com.arnyminerz.filamagenta.ui.section.event.AdminScanner
import com.arnyminerz.filamagenta.ui.shape.BrokenPaperShape
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

private const val BrokenPaperShapeSize = 100f

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalEncodingApi::class, ExperimentalFoundationApi::class,
    ExperimentalUnsignedTypes::class
)
@Composable
@Suppress("LongMethod")
fun EventScreen(
    event: Event,
    viewModel: MainViewModel
) {
    val isAdmin by viewModel.isAdmin.collectAsState(false)
    val loadingOrders by viewModel.isLoadingOrders.collectAsState(false)
    val isDownloadingTickets by viewModel.isDownloadingTickets.collectAsState(false)
    val isUploadingScannedTickets by viewModel.isUploadingScannedTickets.collectAsState(false)

    val orders by Cache.ordersForEvent(event.id).collectListAsState()
    val adminTickets by Cache.adminTicketsForEvent(event.id).collectListAsState()

    val onEditRequested = viewModel::edit.takeIf { isAdmin == true }

    var showingPeopleDialog by remember { mutableStateOf(false) }

    val usersList = adminTickets
        .map { ticket ->
            ticket.isValidated to ticket
        }
        .sortedWith(
            compareBy({ !it.first }, { it.second.customerName })
        )
    val scannedTicketsCount = usersList.count { it.first }

    val hasCamera = PlatformInformation.isCameraSupported()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.cleanName) },
                navigationIcon = {
                    IconButton(
                        onClick = viewModel::stopViewingEvent
                    ) {
                        Icon(Icons.Rounded.ChevronLeft, stringResource(MR.strings.back))
                    }
                },
                actions = {
                    if (isAdmin == true && adminTickets.isNotEmpty()) {
                        Text(
                            text = "$scannedTicketsCount / ${adminTickets.size}",
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable { showingPeopleDialog = true },
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showingPeopleDialog) {
            UsersModalBottomSheet(
                usersList = usersList,
                onDismissRequest = { showingPeopleDialog = false }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 500.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "event-information", contentType = "information") {
                OutlinedCard(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(MR.strings.event_info),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp)
                    )

                    EventInformationRow(
                        headline = stringResource(MR.strings.event_screen_name),
                        text = event.cleanName,
                        onEdit = onEditRequested?.let { { it(EventField.Name) } }
                    )

                    EventInformationRow(
                        headline = stringResource(MR.strings.event_screen_type),
                        text = stringResource((event.type ?: EventType.Unknown).label),
                        onEdit = onEditRequested?.let { { it(EventField.Type) } }
                    )

                    EventInformationRow(
                        headline = stringResource(MR.strings.event_screen_date),
                        text = event.date?.toString()?.replace('T', ' ')
                            ?: stringResource(MR.strings.event_date_unknown),
                        onEdit = onEditRequested?.let { { it(EventField.Date) } }
                    )

                    Spacer(Modifier.height(8.dp))
                }
            }

            if (hasCamera) {
                item(key = "admin-scanner", contentType = "admin-panel") {
                    AdminScanner(
                        onDownloadTicketsRequested = { viewModel.downloadTickets(event.id) },
                        isDownloadingTickets = isDownloadingTickets,
                        onStartScannerRequested = viewModel::startScanner,
                        areTicketsDownloaded = adminTickets.isNotEmpty(),
                        onDeleteTicketsRequested = { viewModel.deleteTickets(event.id) },
                        onSyncTicketsRequested = { viewModel.syncScannedTickets(event.id) },
                        isUploadingScannedTickets = isUploadingScannedTickets
                    )
                }
            }

            item(key = "order-loading-indicator", contentType = "loading-indicator") {
                LoadingCard(
                    visible = loadingOrders && orders.isEmpty(),
                    modifier = Modifier.padding(top = 12.dp),
                    label = stringResource(MR.strings.event_screen_loading_order)
                )
            }

            if (event.hasTicket && orders.isNotEmpty()) {
                val moreThanOne = orders.size > 1
                itemsIndexed(
                    items = orders,
                    span = { _, _ -> GridItemSpan(maxLineSpan) },
                    key = { _, order -> "order-${order.id}" }
                ) { index, order ->
                    OutlinedCard(
                        shape = BrokenPaperShape(BrokenPaperShapeSize),
                        modifier = Modifier
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        val brokenPaperPadding =
                            with(LocalDensity.current) { (BrokenPaperShapeSize / 2).toDp() }

                        var image by remember { mutableStateOf<ByteArray?>(null) }

                        LaunchedEffect(order) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val data = order.qrcode().encrypt()
                                image = Cache.imageCache(data) {
                                    QRCodeGenerator.generate(data)
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = brokenPaperPadding)
                                .padding(top = 4.dp, bottom = 24.dp)
                        ) {
                            Text(
                                text = stringResource(MR.strings.event_screen_ticket_title),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                                textAlign = TextAlign.Center,
                                fontFamily = fontFamilyResource(MR.fonts.VT323.regular),
                                fontSize = 28.sp
                            )
                            Text(
                                text = stringResource(MR.strings.event_screen_ticket_subtitle),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                                textAlign = TextAlign.Center,
                                fontFamily = fontFamilyResource(MR.fonts.VT323.regular),
                                fontSize = 22.sp
                            )

                            Text(
                                text = "- ${event.cleanName} -",
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).padding(top = 8.dp),
                                textAlign = TextAlign.Center,
                                fontFamily = fontFamilyResource(MR.fonts.VT323.regular),
                                fontSize = 22.sp
                            )
                            Text(
                                text = order.customerName + if (moreThanOne) " - $index" else "",
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                                textAlign = TextAlign.Center,
                                fontFamily = fontFamilyResource(MR.fonts.VT323.regular),
                                fontSize = 22.sp
                            )

                            ImageLoader(
                                image = image?.toImageBitmap(),
                                contentDescription = order.orderNumber,
                                modifier = Modifier
                                    .size(256.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 24.dp)
                            )
                            Text(
                                text = "#${order.orderNumber}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontFamily = fontFamilyResource(MR.fonts.VT323.regular),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            item(key = "final-spacer", contentType = "spacer") { Spacer(Modifier.height(12.dp)) }
        }
    }
}
