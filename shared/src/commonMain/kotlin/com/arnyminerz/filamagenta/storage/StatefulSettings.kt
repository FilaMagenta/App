package com.arnyminerz.filamagenta.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier

@Composable
fun ObservableSettings.getBooleanState(key: String, defaultValue: Boolean): MutableState<Boolean> {
    val state = remember { mutableStateOf(getBoolean(key, defaultValue)) }

    DisposableEffect(Unit) {
        val listener = addBooleanListener(key, defaultValue) { state.value = it }

        // Update the value stored initially
        state.value = getBoolean(key, defaultValue)

        onDispose { listener.deactivate() }
    }

    LaunchedEffect(state.value) {
        if (getBoolean(key, defaultValue) != state.value) {
            Napier.v("Updating value of $key to ${state.value}")
            set(key, state.value)
        }
    }

    return state
}
