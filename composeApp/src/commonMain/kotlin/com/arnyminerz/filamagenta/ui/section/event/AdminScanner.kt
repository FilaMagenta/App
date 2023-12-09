package com.arnyminerz.filamagenta.ui.section.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.device.PlatformInformation
import dev.icerock.moko.resources.compose.stringResource

@Suppress("LongParameterList")
@Composable
fun AdminScanner(
    onDownloadTicketsRequested: () -> Unit,
    isDownloadingTickets: Boolean,
    onStartScannerRequested: () -> Unit,
    areTicketsDownloaded: Boolean,
    areTicketsDownloadedIncludesExternal: Boolean,
    onDeleteTicketsRequested: () -> Unit,
    isExportingTickets: Boolean,
    onExportTicketsRequested: () -> Unit,
    onSyncTicketsRequested: () -> Unit,
    isUploadingScannedTickets: Boolean,
    onPickExternalDatabaseRequested: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(MR.strings.event_screen_admin_scanner),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (PlatformInformation.isNfcSupported()) {
                Icon(
                    imageVector = Icons.Outlined.Nfc,
                    contentDescription = stringResource(MR.strings.event_screen_admin_scanner_nfc_supported)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            OutlinedButton(
                onClick = onDownloadTicketsRequested,
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                enabled = !isDownloadingTickets
            ) {
                Text(
                    text = stringResource(MR.strings.event_screen_admin_scanner_download),
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                AnimatedVisibility(isDownloadingTickets) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                }
            }

            IconButton(
                onClick = onStartScannerRequested,
                enabled = areTicketsDownloadedIncludesExternal && !isDownloadingTickets
            ) {
                Icon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    contentDescription = stringResource(MR.strings.event_screen_admin_scanner_scan)
                )
            }
            AnimatedVisibility(
                visible = areTicketsDownloaded
            ) {
                IconButton(
                    onClick = onDeleteTicketsRequested,
                    enabled = !isDownloadingTickets
                ) {
                    Icon(Icons.Rounded.Delete, stringResource(MR.strings.delete))
                }
            }
            AnimatedVisibility(
                visible = areTicketsDownloadedIncludesExternal
            ) {
                IconButton(
                    onClick = onExportTicketsRequested,
                    enabled = !isExportingTickets
                ) {
                    Icon(Icons.Rounded.FileDownload, stringResource(MR.strings.export))
                }
            }
            AnimatedVisibility(
                visible = !areTicketsDownloadedIncludesExternal
            ) {
                IconButton(
                    onClick = onPickExternalDatabaseRequested
                ) {
                    Icon(Icons.Rounded.FileOpen, null)
                }
            }
        }

        AnimatedVisibility(visible = areTicketsDownloaded) {
            Column {
                Text(
                    text = stringResource(MR.strings.event_screen_admin_scanner_sync_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp)
                )
                Text(
                    text = stringResource(MR.strings.event_screen_admin_scanner_sync_message),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )
                OutlinedButton(
                    onClick = onSyncTicketsRequested,
                    modifier = Modifier.padding(8.dp).align(Alignment.End),
                    enabled = !isUploadingScannedTickets && areTicketsDownloaded
                ) {
                    Text(stringResource(MR.strings.synchronize))
                }
            }
        }
    }
}
