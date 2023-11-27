package com.arnyminerz.filamagenta.ui.logic

import com.arnyminerz.filamagenta.android.MainActivity

var mainActivity: MainActivity? = null

actual fun onApplicationEndRequested() {
    mainActivity?.finish()
}
