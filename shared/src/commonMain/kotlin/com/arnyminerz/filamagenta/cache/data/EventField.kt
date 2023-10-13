package com.arnyminerz.filamagenta.cache.data

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

sealed class EventField<T>(
    val displayName: StringResource,
    val editor: EventFieldEditor<T>
) {
    val value: T? get() = editor.value.value

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
        onSubmit: () -> Job,
        onDismissRequest: () -> Unit
    ) {
        var isLoading by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                if (!isLoading) {
                    mutableValue.value = null
                    onDismissRequest()
                }
            },
            title = { Text(title) },
            confirmButton = {
                TextButton(
                    onClick = {
                        isLoading = true
                        onSubmit().invokeOnCompletion {
                            isLoading = false

                            mutableValue.value = null
                            onDismissRequest()
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(stringResource(MR.strings.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mutableValue.value = null
                        onDismissRequest()
                    },
                    enabled = !isLoading
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

    @OptIn(ExperimentalMaterial3Api::class)
    class Date: EventFieldEditor<LocalDateTime>() {

        private fun updateValue(datePickerState: DatePickerState, timePickerState: TimePickerState) {
            mutableValue.value = datePickerState.selectedDateMillis?.let { dateMillis ->
                val date = Instant.fromEpochMilliseconds(dateMillis)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                LocalDateTime(
                    year = date.year,
                    month = date.month,
                    dayOfMonth = date.dayOfMonth,
                    hour = timePickerState.hour,
                    minute = timePickerState.minute
                )
            }
        }

        @Composable
        override fun Content() {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds()
            )
            val timePickerState = rememberTimePickerState(
                is24Hour = true
            )

            var displayingDatePicker by remember { mutableStateOf(false) }
            if (displayingDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { displayingDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                updateValue(datePickerState, timePickerState)
                                displayingDatePicker = false
                            }
                        ) {
                            Text(stringResource(MR.strings.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { displayingDatePicker = false }
                        ) {
                            Text(stringResource(MR.strings.cancel))
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }

            var displayingTimePicker by remember { mutableStateOf(false) }
            if (displayingTimePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        updateValue(datePickerState, timePickerState)
                        displayingTimePicker = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                updateValue(datePickerState, timePickerState)
                                displayingTimePicker = false
                            }
                        ) {
                            Text(stringResource(MR.strings.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { displayingTimePicker = false }
                        ) {
                            Text(stringResource(MR.strings.cancel))
                        }
                    }
                ) {
                    TimePicker(
                        state = timePickerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val value by value.collectAsState()
                Text(
                    text = value?.toString()?.replace('T', ' ') ?: ""
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { displayingDatePicker = true },
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    ) {
                        Text(stringResource(MR.strings.event_editor_date))
                    }
                    OutlinedButton(
                        onClick = { displayingTimePicker = true },
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    ) {
                        Text(stringResource(MR.strings.event_editor_time))
                    }
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
