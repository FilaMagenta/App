package com.arnyminerz.filamagenta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationBarItem(
    val icon: ImageVector,
    val label: @Composable () -> String,
    val contentDescription: @Composable () -> String = label
) {
    @Composable
    fun Icon() {
        androidx.compose.material3.Icon(icon, contentDescription())
    }
}
