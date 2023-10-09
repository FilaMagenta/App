package com.arnyminerz.filamagenta.ui.reusable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun EventInformationRow(
    headline: String,
    text: String,
    onEdit: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
        onEdit?.let { onClick ->
            IconButton(onClick) {
                Icon(Icons.Outlined.Edit, stringResource(MR.strings.edit))
            }
        }
    }
}
