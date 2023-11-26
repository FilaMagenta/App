package com.arnyminerz.filamagenta.bluetooth

import android.app.Application
import com.arnyminerz.filamagenta.android.applicationContext
import dev.bluefalcon.ApplicationContext

actual val bluetoothContext: ApplicationContext get() = applicationContext as Application
