package com.arnyminerz.filamagenta.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.cleanName
import com.arnyminerz.filamagenta.cache.data.isComplete
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventItem(
    event: Event,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .then(modifier),
        colors = if (event.isComplete) {
            CardDefaults.outlinedCardColors()
        } else {
            CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = (event.type ?: EventType.Unknown).icon,
                    contentDescription = event.type?.label?.let { stringResource(it) }
                )
                Text(
                    text = remember { event.cleanName },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
            Text(
                text = stringResource(
                    MR.strings.event_date,
                    event.date?.toString() ?: stringResource(MR.strings.event_date_unknown)
                ),
                style = MaterialTheme.typography.labelLarge,
            )
            if (!event.variations.isNullOrEmpty()) {
                Text(
                    text = stringResource(MR.strings.event_options_available),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!event.isComplete) {
                ProvideTextStyle(
                    MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                ) {
                    Text(
                        text = stringResource(MR.strings.event_warnings),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (event.date == null) {
                        Text("- " + stringResource(MR.strings.event_warnings_date))
                    }
                    if (event.type == null) {
                        Text("- " + stringResource(MR.strings.event_warnings_type))
                    }
                }
            }
        }
    }
}
