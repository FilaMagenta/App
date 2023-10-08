package com.arnyminerz.filamagenta.android

import android.app.Application
import com.arnyminerz.filamagenta.account.AccountsProvider
import com.arnyminerz.filamagenta.device.PlatformInformation
import com.arnyminerz.filamagenta.network.CustomBrowserUi
import com.arnyminerz.filamagenta.storage.SettingsFactoryProvider
import com.arnyminerz.filamagenta.storage.settingsFactory

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        settingsFactory = SettingsFactoryProvider(this).factory

        AccountsProvider(this).provide()
        CustomBrowserUi.provideApplicationContext(this)
        PlatformInformation.checkIfCustomTabsSupported(this)
    }
}
