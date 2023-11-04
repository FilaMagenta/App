package com.arnyminerz.filamagenta.ui.logic

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalComposeUiApi::class)
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val width = windowInfo.containerSize.width
    val widthDp = with(density) { width.toDp() }

    return if (widthDp < 600.dp) {
        WindowSizeClass.Compact
    } else if (widthDp < 870.dp) {
        WindowSizeClass.Medium
    } else {
        WindowSizeClass.Expanded
    }
}
