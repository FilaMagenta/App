package com.arnyminerz.filamagenta.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun GenericErrorDialog(throwable: Throwable, onDismissRequest: () -> Unit) {
    val errorString = buildAnnotatedString {
        appendLine(stringResource(MR.strings.error_dialog_message))

        appendLine(stringResource(MR.strings.error_dialog_msg))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)) {
            appendLine(throwable.message)
        }
        appendLine()

        appendLine(stringResource(MR.strings.error_dialog_trace))
        withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
            appendLine(throwable.stackTraceToString())
        }
        appendLine()
    }

    ErrorDialog(errorString, onDismissRequest)
}
