package com.arnyminerz.filamagenta.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set

@Composable
fun ObservableSettings.getBooleanState(key: String, defaultValue: Boolean): MutableState<Boolean> {
    val state = remember { mutableStateOf(defaultValue) }

    DisposableEffect(Unit) {
        val listener = addBooleanListener(key, defaultValue) { state.value = it }

        onDispose { listener.deactivate() }
    }

    LaunchedEffect(state) {
        if (getBoolean(key, defaultValue) != state.value) {
            set(key, state.value)
        }
    }

    return state
}
