package com.arnyminerz.filamagenta.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun UpdateDialog(onInstallRequest: () -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(MR.strings.update_available_dialog_title)) },
        text = { Text(stringResource(MR.strings.update_available_dialog_message)) },
        confirmButton = {
            TextButton(
                onClick = onInstallRequest
            ) {
                Text(stringResource(MR.strings.install))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(MR.strings.cancel))
            }
        }
    )
}
