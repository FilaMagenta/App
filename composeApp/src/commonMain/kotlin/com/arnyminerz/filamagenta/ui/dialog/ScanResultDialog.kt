package com.arnyminerz.filamagenta.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.data.QrCodeScanResult
import com.arnyminerz.filamagenta.sound.SoundPlayer
import com.arnyminerz.filamagenta.ui.theme.ExtendedColors
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultDialog(
    result: QrCodeScanResult,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LaunchedEffect(result) {
            when (result) {
                is QrCodeScanResult.Success -> {
                    SoundPlayer.playFromResources("sounds/success.wav")
                }

                is QrCodeScanResult.Invalid,
                is QrCodeScanResult.AlreadyUsed,
                is QrCodeScanResult.NotViewingEvent,
                is QrCodeScanResult.TicketListNotDownloaded -> {
                    SoundPlayer.playFromResources("sounds/error.wav")
                }

                is QrCodeScanResult.Loading -> {}
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = when (result) {
                    is QrCodeScanResult.Success -> Icons.Rounded.Verified
                    is QrCodeScanResult.AlreadyUsed -> Icons.Rounded.NewReleases
                    is QrCodeScanResult.Invalid -> Icons.Rounded.ErrorOutline
                    is QrCodeScanResult.NotViewingEvent -> Icons.Rounded.WarningAmber
                    is QrCodeScanResult.TicketListNotDownloaded -> Icons.Rounded.FileDownload
                    is QrCodeScanResult.Loading -> Icons.Rounded.HourglassEmpty
                },
                contentDescription = null,
                tint = when (result) {
                    is QrCodeScanResult.Success -> ExtendedColors.Positive.color()
                    is QrCodeScanResult.AlreadyUsed -> ExtendedColors.Warning.color()
                    is QrCodeScanResult.Invalid -> ExtendedColors.Negative.color()
                    is QrCodeScanResult.NotViewingEvent -> ExtendedColors.Warning.color()
                    is QrCodeScanResult.TicketListNotDownloaded -> ExtendedColors.Warning.color()
                    is QrCodeScanResult.Loading -> ExtendedColors.Neutral.color()
                },
                modifier = Modifier.size(128.dp).padding(top = 32.dp)
            )

            Text(
                text = when (result) {
                    is QrCodeScanResult.Success -> stringResource(MR.strings.scan_result_success)
                    is QrCodeScanResult.AlreadyUsed -> stringResource(MR.strings.scan_result_reused)
                    is QrCodeScanResult.Invalid -> stringResource(MR.strings.scan_result_invalid)
                    is QrCodeScanResult.NotViewingEvent -> stringResource(MR.strings.scan_result_not_viewing)
                    is QrCodeScanResult.TicketListNotDownloaded -> stringResource(MR.strings.scan_result_tickets_empty)
                    is QrCodeScanResult.Loading -> stringResource(MR.strings.scan_result_loading)
                },
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            if (result is QrCodeScanResult.Success) {
                Text(
                    text = result.customerName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "#${result.orderNumber}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
