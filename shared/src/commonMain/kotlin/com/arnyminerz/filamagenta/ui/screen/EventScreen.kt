package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
                .padding(horizontal = 8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            EventInformationRow(
                headline = stringResource(MR.strings.event_screen_name),
                text = event.cleanName,
                onEdit = onEditRequested?.let { { it(EventField.Name) } }
            )

            Spacer(Modifier.height(8.dp))

            EventInformationRow(
                headline = stringResource(MR.strings.event_screen_type),
                text = stringResource((event.type ?: EventType.Unknown).label),
                onEdit = onEditRequested?.let { { it(EventField.Type) } }
            )

            Spacer(Modifier.height(8.dp))

            EventInformationRow(
                headline = stringResource(MR.strings.event_screen_date),
                text = event.date?.toString() ?: stringResource(MR.strings.event_date_unknown),
                onEdit = onEditRequested?.let { { it(EventField.Date) } }
            )
        }
    }
}
