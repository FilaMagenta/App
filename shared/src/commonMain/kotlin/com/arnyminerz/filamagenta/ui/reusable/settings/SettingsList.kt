package com.arnyminerz.filamagenta.ui.reusable.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun <T : Any> SettingsList(
    default: T?,
    options: List<T>,
    headline: String,
    dialogTitle: String,
    modifier: Modifier = Modifier,
    toString: @Composable (T) -> String = { it.toString() },
    leadingContent: @Composable (() -> Unit)? = null,
    onOptionSelected: (T) -> Unit
) {
    var isShowingDialog by remember { mutableStateOf(false) }
    if (isShowingDialog) {
        var selection by remember { mutableStateOf(default) }

        AlertDialog(
            onDismissRequest = { isShowingDialog = false },
            title = { Text(dialogTitle) },
            text = {
                LazyColumn {
                    items(options) { option ->
                        ListItem(
                            headlineContent = { Text(toString(option)) },
                            leadingContent = {
                                Icon(
                                    imageVector = if (option == selection)
                                        Icons.Rounded.RadioButtonChecked
                                    else
                                        Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = toString(option)
                                )
                            },
                            modifier = Modifier.clickable { selection = option }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onOptionSelected(selection!!); isShowingDialog = false },
                    enabled = selection != null
                ) { Text(stringResource(MR.strings.ok)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { isShowingDialog = false }
                ) { Text(stringResource(MR.strings.cancel)) }
            }
        )
    }

    ListItem(
        headlineContent = { Text(headline) },
        modifier = Modifier
            .clickable { isShowingDialog = true }
            .then(modifier),
        leadingContent = leadingContent
    )
}