package com.arnyminerz.filamagenta.ui.logic

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val context = LocalContext.current

    return if (context is Activity) {
        // if context is Activity, use native method
        when(calculateWindowSizeClass(context).widthSizeClass) {
            WindowWidthSizeClass.Compact -> WindowSizeClass.Compact
            WindowWidthSizeClass.Medium -> WindowSizeClass.Medium
            WindowWidthSizeClass.Expanded -> WindowSizeClass.Expanded
            else -> WindowSizeClass.Expanded
        }
    } else {
        // otherwise use manual method
        val widthDp = LocalConfiguration.current.screenWidthDp.dp

        if (widthDp < 600.dp) {
            WindowSizeClass.Compact
        } else if (widthDp < 870.dp) {
            WindowSizeClass.Medium
        } else {
            WindowSizeClass.Expanded
        }
    }
}
