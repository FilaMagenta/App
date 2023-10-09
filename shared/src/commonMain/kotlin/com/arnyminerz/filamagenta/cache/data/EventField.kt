package com.arnyminerz.filamagenta.cache.data

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class EventField<T>(
    val displayName: StringResource,
    val editor: EventFieldEditor<T>
) {
    data object Name : EventField<String>(
        MR.strings.event_field_name,
        EventFieldEditor.String { stringResource(MR.strings.event_field_name) }
    )

    data object Date : EventField<String>(
        MR.strings.event_field_date,
        EventFieldEditor.String { stringResource(MR.strings.event_field_date) }
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
