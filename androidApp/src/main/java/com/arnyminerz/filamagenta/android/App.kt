package com.arnyminerz.filamagenta.android

import android.app.Application
import com.arnyminerz.filamagenta.account.AccountsProvider
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.DriverFactory
import com.arnyminerz.filamagenta.cache.createDatabase
import com.arnyminerz.filamagenta.diagnostics.SentryInitializer
import com.arnyminerz.filamagenta.storage.SettingsFactoryProvider
import com.arnyminerz.filamagenta.storage.settingsFactory

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        SentryInitializer(this).init()

        settingsFactory = SettingsFactoryProvider(this).factory

        AccountsProvider(this).provide()

        createDatabase(
            DriverFactory(this)
        )

        accounts.startWatchingAccounts(mainLooper)
    }

    override fun onTerminate() {
        super.onTerminate()

        accounts.stopWatchingAccounts()
    }
}
