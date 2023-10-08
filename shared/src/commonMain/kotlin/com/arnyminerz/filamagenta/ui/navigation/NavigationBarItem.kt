package com.arnyminerz.filamagenta.ui.navigation

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationBarItem(
    val icon: ImageVector,
    val label: @Composable () -> String,
    val contentDescription: @Composable () -> String = label,
    val content: @Composable BoxScope.() -> Unit
) {
    @Composable
    fun Icon() {
        androidx.compose.material3.Icon(icon, contentDescription())
    }
}
