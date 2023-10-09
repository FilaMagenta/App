package com.arnyminerz.filamagenta.cache.data

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

sealed class EventField<T>(
    val displayName: StringResource,
    val editor: EventFieldEditor<T>
) {
    data object Name : EventField<String>(
        MR.strings.event_field_name,
        EventFieldEditor.String { stringResource(MR.strings.event_field_name) }
    )

    data object Date : EventField<LocalDateTime>(
        MR.strings.event_field_date,
        EventFieldEditor.Date()
    )

    data object Type : EventField<EventType>(
        MR.strings.event_field_type,
        EventFieldEditor.Enum(
            { stringResource(MR.strings.event_field_type) },
            EventType.entries
        )
    )
}

sealed class EventFieldEditor<Type> {
    protected val mutableValue = MutableStateFlow<Type?>(null)
    val value: StateFlow<Type?> get() = mutableValue

    @Composable
    fun Dialog(
        title: kotlin.String,
        onSubmit: () -> Unit,
        onDismissRequest: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = {
                mutableValue.value = null
                onDismissRequest()
            },
            title = { Text(title) },
            confirmButton = {
                TextButton(
                    onClick = onSubmit
                ) {
                    Text(stringResource(MR.strings.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mutableValue.value = null
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(MR.strings.cancel))
                }
            },
            text = { Content() }
        )
    }

    @Composable
    abstract fun Content()

    open fun toString(value: Type?): kotlin.String {
        return value?.toString() ?: ""
    }

    class String(private val label: @Composable () -> kotlin.String) : EventFieldEditor<kotlin.String>() {
        @Composable
        override fun Content() {
            val value by this.mutableValue.collectAsState()

            OutlinedTextField(
                value = value ?: "",
                onValueChange = { mutableValue.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label()) }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    class Date(): EventFieldEditor<LocalDateTime>() {
        @Composable
        fun ToggleButton(toggled: Boolean, text: kotlin.String, modifier: Modifier = Modifier, onClick: () -> Unit) {
            if (toggled) {
                Button(
                    onClick = onClick,
                    modifier = modifier
                ) {
                    Text(text)
                }
            } else {
                OutlinedButton(
                    onClick = onClick,
                    modifier = modifier
                ) {
                    Text(text)
                }
            }
        }

        @Composable
        override fun Content() {
            val scope = rememberCoroutineScope()
            val pagerState = rememberPagerState { 2 }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ToggleButton(
                        toggled = pagerState.currentPage == 0,
                        text = stringResource(MR.strings.event_editor_date),
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) { scope.launch { pagerState.animateScrollToPage(0) } }

                    ToggleButton(
                        toggled = pagerState.currentPage == 1,
                        text = stringResource(MR.strings.event_editor_time),
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) { scope.launch { pagerState.animateScrollToPage(1) } }
                }
                HorizontalPager(
                    state = pagerState
                ) { page ->
                    Text("page: $page")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    class Enum<E : kotlin.Enum<E>>(
        private val label: @Composable () -> kotlin.String,
        private val entries: List<E>
    ) : EventFieldEditor<E>() {
        @Composable
        override fun Content() {
            var expanded by remember { mutableStateOf(false) }
            val value by this.mutableValue.collectAsState()

            ExposedDropdownMenuBox(
                expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = toString(value),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text(label()) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )

                ExposedDropdownMenu(
                    expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    for (entry in entries) {
                        DropdownMenuItem(
                            text = { Text(toString(entry)) },
                            onClick = {
                                mutableValue.value = entry
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
