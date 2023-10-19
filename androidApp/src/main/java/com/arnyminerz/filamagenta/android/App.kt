package com.arnyminerz.filamagenta.android

import android.app.Application
import android.content.pm.PackageManager
import com.arnyminerz.filamagenta.account.AccountsProvider
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.DriverFactory
import com.arnyminerz.filamagenta.cache.createDatabase
import com.arnyminerz.filamagenta.device.PlatformInformation
import com.arnyminerz.filamagenta.diagnostics.SentryInitializer
import com.arnyminerz.filamagenta.storage.SettingsFactoryProvider
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.storage.settingsFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        settingsFactory = SettingsFactoryProvider(this).factory

        if (settings.getBoolean(SettingsKeys.DATA_COLLECTION, true)) {
            SentryInitializer(this).init()
        }

        PlatformInformation.hasCameraFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        PlatformInformation.hasNfcFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)

        AccountsProvider(this).provide()

        createDatabase(
            DriverFactory(this)
        )

        accounts.startWatchingAccounts(mainLooper)
    }

    override fun onTerminate() {
        super.onTerminate()

        accounts.stopWatchingAccounts()

        // Remove all viewing states
        settings.remove(SettingsKeys.SYS_VIEWING_EVENT)
    }
}
