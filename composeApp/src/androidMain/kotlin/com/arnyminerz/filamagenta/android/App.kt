package com.arnyminerz.filamagenta.android

import android.app.Application
import android.content.pm.PackageManager
import android.os.Environment
import com.arnyminerz.filamagenta.account.AccountsProvider
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.DriverFactory
import com.arnyminerz.filamagenta.cache.createDatabase
import com.arnyminerz.filamagenta.device.FSInformation
import com.arnyminerz.filamagenta.device.PlatformInformation
import com.arnyminerz.filamagenta.diagnostics.SentryDiagnostics
import com.arnyminerz.filamagenta.lifecycle.initialize
import com.arnyminerz.filamagenta.sound.SoundPlayer
import com.arnyminerz.filamagenta.storage.SettingsFactoryProvider
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.storage.settingsFactory
import com.arnyminerz.filamagenta.worker.SyncWorker
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        initialize()

        settingsFactory = SettingsFactoryProvider(this).factory

        SentryDiagnostics.initialize()

        PlatformInformation.hasCameraFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        PlatformInformation.hasNfcFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)

        FSInformation.downloadsDirectory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

        createDatabase(
            DriverFactory(this)
        )

        AccountsProvider(this).provide()
        accounts.startWatchingAccounts(mainLooper)

        SoundPlayer.setCacheDirectory(cacheDir)

        CoroutineScope(Dispatchers.IO).launch {
            Napier.i { "Scheduling the synchronization worker..." }
            SyncWorker.schedule(this@App)

            Napier.i { "Synchronization worker scheduled correctly." }
        }
    }

    override fun onTerminate() {
        super.onTerminate()

        accounts.stopWatchingAccounts()

        // Remove all viewing states
        settings.remove(SettingsKeys.SYS_VIEWING_EVENT)
    }
}
