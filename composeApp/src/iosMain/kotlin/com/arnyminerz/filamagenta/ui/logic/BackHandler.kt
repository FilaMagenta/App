package com.arnyminerz.filamagenta.ui.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import store

// https://exyte.com/blog/jetpack-compose-multiplatform
@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
    LaunchedEffect(isEnabled) {
        store.events.collect {
            if(isEnabled) {
                onBack()
            }
        }
    }
}
