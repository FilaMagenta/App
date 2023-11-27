package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.arnyminerz.filamagenta.ui.reusable.LoadingBox

object LoadingScreen: Screen {
    @Composable
    override fun Content() {
        LoadingBox()
    }
}
