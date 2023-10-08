package com.arnyminerz.filamagenta.ui.state

import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {
    val scope: CoroutineScope
}
