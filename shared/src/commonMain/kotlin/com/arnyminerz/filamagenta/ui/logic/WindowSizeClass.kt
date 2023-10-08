package com.arnyminerz.filamagenta.ui.logic

import androidx.compose.runtime.Composable

enum class WindowSizeClass {
    Compact, Medium, Expanded
}

@Composable
expect fun calculateWindowSizeClass(): WindowSizeClass
