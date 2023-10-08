package com.arnyminerz.filamagenta.ui.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

actual abstract class ViewModel {
    actual val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
}
