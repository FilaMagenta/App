package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.cache.Event
import com.arnyminerz.filamagenta.cache.data.EventField
import com.arnyminerz.filamagenta.cache.data.EventType
import com.arnyminerz.filamagenta.cache.data.cleanName
import com.arnyminerz.filamagenta.ui.reusable.EventInformationRow
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    event: Event,
    onEditRequested: ((field: EventField<*>) -> Unit)?,
    onBackRequested: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.cleanName) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackRequested
                    ) {
                        Icon(Icons.Rounded.ChevronLeft, stringResource(MR.strings.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(MR.strings.event_info),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 8.dp)
                )

                EventInformationRow(
                    headline = stringResource(MR.strings.event_screen_name),
                    text = event.cleanName,
                    onEdit = onEditRequested?.let { { it(EventField.Name) } }
                )

                EventInformationRow(
                    headline = stringResource(MR.strings.event_screen_type),
                    text = stringResource((event.type ?: EventType.Unknown).label),
                    onEdit = onEditRequested?.let { { it(EventField.Type) } }
                )

                EventInformationRow(
                    headline = stringResource(MR.strings.event_screen_date),
                    text = event.date?.toString()?.replace('T', ' ') ?: stringResource(MR.strings.event_date_unknown),
                    onEdit = onEditRequested?.let { { it(EventField.Date) } }
                )

                Spacer(Modifier.height(8.dp))
            }

            if (!event.variations.isNullOrEmpty()) {
                for (variation in event.variations) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = variation.name,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Row {
                            for (option in variation.options) {
                                FilterChip(
                                    selected = false,
                                    label = { Text(option) },
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
