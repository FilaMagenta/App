package com.arnyminerz.filamagenta.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier

@Composable
private fun <T: Any> ObservableSettings.getState(
    key: String,
    defaultValue: T,
    addListener: ObservableSettings.(key: String, defaultValue: T, callback: (T) -> Unit) -> SettingsListener,
    getter: ObservableSettings.(key: String, defaultValue: T) -> T,
    setter: ObservableSettings.(key: String, value: T) -> Unit
): MutableState<T> {
    val state = remember { mutableStateOf(getter(key, defaultValue)) }

    DisposableEffect(state) {
        val listener = addListener(key, defaultValue) { state.value = it }

        // Update the value stored initially
        state.value = getter(key, defaultValue)

        onDispose { listener.deactivate() }
    }

    LaunchedEffect(state.value) {
        if (getter(key, defaultValue) != state.value) {
            Napier.v("Updating value of $key to ${state.value}")
            setter(key, state.value)
        }
    }

    return state
}

@Composable
fun ObservableSettings.getBooleanState(key: String, defaultValue: Boolean): MutableState<Boolean> {
    return getState(
        key,
        defaultValue,
        ObservableSettings::addBooleanListener,
        ObservableSettings::getBoolean
    ) { k, v -> this[k] = v }
}

@Composable
fun ObservableSettings.getStringState(key: String, defaultValue: String): MutableState<String> {
    return getState(
        key,
        defaultValue,
        ObservableSettings::addStringListener,
        ObservableSettings::getString
    ) { k, v -> this[k] = v }
}
