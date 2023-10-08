package com.arnyminerz.filamagenta.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

actual abstract class ViewModel: ViewModel() {
    actual val scope: CoroutineScope = viewModelScope
}
