package com.arnyminerz.filamagenta.ui.logic

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
    BackHandler(isEnabled, onBack)
}
