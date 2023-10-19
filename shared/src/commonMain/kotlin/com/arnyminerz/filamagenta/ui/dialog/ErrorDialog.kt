package com.arnyminerz.filamagenta.ui.dialog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ErrorDialog(errorString: AnnotatedString, onDismissRequest: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(MR.strings.error_dialog_title)) },
        text = {
            SelectionContainer(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            ) {
                Text(errorString)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { uriHandler.openUri("https://status.arnyminerz.com/status/filamagenta") }
            ) {
                Text(stringResource(MR.strings.server_status))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { clipboardManager.setText(errorString) }
            ) {
                Text(stringResource(MR.strings.copy))
            }
        }
    )
}
