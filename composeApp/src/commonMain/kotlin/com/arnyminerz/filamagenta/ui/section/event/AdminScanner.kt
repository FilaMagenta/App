package com.arnyminerz.filamagenta.ui.section.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.outlined.BluetoothSearching
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.SettingsBluetooth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.bluetooth.BluetoothPermissions
import com.arnyminerz.filamagenta.bluetooth.bluetoothContext
import com.arnyminerz.filamagenta.device.PlatformInformation
import dev.bluefalcon.BlueFalcon
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

const val BLEServiceUUID = "19B10010-E8F2-537E-4F6C-D104768A1214"

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
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(MR.strings.event_screen_admin_scanner),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (PlatformInformation.isBLESupported()) {
                val blueFalcon = BlueFalcon(bluetoothContext, null) // BLEServiceUUID
                var isScanning by remember { mutableStateOf(false) }
                var arePermissionsGranted by remember { mutableStateOf(BluetoothPermissions.arePermissionGranted()) }
                var isBtEnabled by remember { mutableStateOf(BluetoothPermissions.isBluetoothEnabled()) }

                LaunchedEffect(Unit) {
                    blueFalcon.peripherals.collect { peripherals ->
                        Napier.i {
                            "Found ${peripherals.size} peripherals:\n" +
                                peripherals.joinToString("\n- ") { "${it.uuid} :: ${it.name}" }
                        }
                    }
                }

                Icon(
                    imageVector = if (!arePermissionsGranted)
                        Icons.Outlined.SettingsBluetooth
                    else if (!isBtEnabled)
                        Icons.Outlined.BluetoothDisabled
                    else if (isScanning)
                        Icons.Outlined.BluetoothSearching
                    else
                        Icons.Outlined.Bluetooth,
                    contentDescription = stringResource(MR.strings.event_screen_admin_scanner_ble_connect),
                    modifier = Modifier.clickable {
                        if (!arePermissionsGranted) {
                            CoroutineScope(Dispatchers.IO).launch {
                                BluetoothPermissions.requestPermissions()

                                arePermissionsGranted = BluetoothPermissions.arePermissionGranted()
                            }
                        } else if (!isBtEnabled) {
                            CoroutineScope(Dispatchers.IO).launch {
                                BluetoothPermissions.enableBluetooth()
                            }
                        } else if (blueFalcon.isScanning) {
                            // blueFalcon.stopScanning()
                        } else {
                            // blueFalcon.scan()
                            BluetoothPermissions.scanForDevices()
                        }
                        isScanning = blueFalcon.isScanning
                    }
                )
            }
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

        AnimatedVisibility(visible = areTicketsDownloaded) {
            Column {
                Text(
                    text = stringResource(MR.strings.event_screen_admin_scanner_sync_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp)
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
