package com.arnyminerz.filamagenta.ui.section.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
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
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AdminScanner(
    onDownloadTicketsRequested: () -> Unit,
    isDownloadingTickets: Boolean,
    onStartScannerRequested: () -> Unit,
    areTicketsDownloaded: Boolean,
    onDeleteTicketsRequested: () -> Unit,
    onSyncTicketsRequested: () -> Unit,
    isUploadingScannedTickets: Boolean
) {
    OutlinedCard(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(MR.strings.event_screen_admin_scanner),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp)
        )
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
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                enabled = areTicketsDownloaded && !isDownloadingTickets
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
        }

        Text(
            text = stringResource(MR.strings.event_screen_admin_scanner_sync_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp)
        )
        Text(
            text = stringResource(MR.strings.event_screen_admin_scanner_sync_message),
            style = MaterialTheme.typography.bodyLarge,
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
