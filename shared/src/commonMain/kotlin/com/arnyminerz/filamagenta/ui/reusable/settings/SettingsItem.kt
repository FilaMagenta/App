package com.arnyminerz.filamagenta.ui.reusable.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsItem(
    headline: String,
    summary: String?,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    contentDescription: String? = headline,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.clickable(enabled = onClick != null, onClick = { onClick?.invoke() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = contentDescription,
                modifier = Modifier
                    .padding(start = 8.dp, end = 12.dp)
                    .padding(vertical = 8.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium
            )
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Light
                )
            }
        }
        trailingContent?.invoke()
    }
}
