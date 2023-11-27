package com.arnyminerz.filamagenta.ui.reusable

import androidx.compose.runtime.Composable

/**
 * Uses [LoadingBox], but only displays it when [visible] is `true`.
 */
@Composable
fun LoadingIndicator(visible: Boolean) {
    LoadingBox()
}
