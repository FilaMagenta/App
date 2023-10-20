package com.arnyminerz.filamagenta.android

import android.app.Application
import android.content.pm.PackageManager
import com.arnyminerz.filamagenta.account.AccountsProvider
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.DriverFactory
import com.arnyminerz.filamagenta.cache.createDatabase
import com.arnyminerz.filamagenta.device.Diagnostics
import com.arnyminerz.filamagenta.device.MeasurementScope
import com.arnyminerz.filamagenta.device.PerformanceMeasurer
import com.arnyminerz.filamagenta.device.PlatformInformation
import com.arnyminerz.filamagenta.diagnostics.SentryInformation
import com.arnyminerz.filamagenta.lifecycle.initialize
import com.arnyminerz.filamagenta.sound.SoundPlayer
import com.arnyminerz.filamagenta.storage.SettingsFactoryProvider
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.storage.settingsFactory
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import io.sentry.protocol.User

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        initialize()

        settingsFactory = SettingsFactoryProvider(this).factory

        SentryAndroid.init(this) { options ->
            options.dsn = SentryInformation.SentryDsn
            options.isEnabled = settings.getBoolean(SettingsKeys.DATA_COLLECTION, true)
            options.release = SentryInformation.ReleaseName
            options.dist = SentryInformation.ReleaseName.substringBefore('-')
            options.environment = if (SentryInformation.IsProduction) "prod" else "dev"
            options.tracesSampleRate = 1.0
        }
        Diagnostics.updateUserInformation = { username, email ->
            val user = User()
            user.username = username
            user.email = email
            Sentry.setUser(user)
        }
        Diagnostics.deleteUserInformation = { Sentry.setUser(null) }
        Diagnostics.performance = object : PerformanceMeasurer {
            private inline fun <Result> perform(
                name: String,
                operation: String,
                metadata: Map<String, Any>,
                measurements: Map<String, Number>,
                block: MeasurementScope.() -> Result
            ): Result {
                val transaction = Sentry.startTransaction(name, operation)
                for ((key, value) in metadata) {
                    transaction.setData(key, value)
                }
                for ((key, value) in measurements) {
                    transaction.setMeasurement(key, value)
                }
                return try {
                    block(
                        object : MeasurementScope {
                            override fun setMetadata(key: String, value: Any) {
                                transaction.setData(key, value)
                            }

                            override fun setMeasurement(key: String, value: Number) {
                                transaction.setMeasurement(key, value)
                            }
                        }
                    )
                } finally {
                    transaction.finish()
                }
            }

            override operator fun <Result> invoke(
                name: String,
                operation: String,
                metadata: Map<String, Any>,
                measurements: Map<String, Number>,
                block: MeasurementScope.() -> Result
            ): Result {
                return perform(name, operation, metadata, measurements) { block() }
            }

            override suspend fun <Result> suspending(
                name: String,
                operation: String,
                metadata: Map<String, Any>,
                measurements: Map<String, Number>,
                block: suspend MeasurementScope.() -> Result
            ): Result {
                return perform(name, operation, metadata, measurements) { block() }
            }
        }

        PlatformInformation.hasCameraFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        PlatformInformation.hasNfcFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)

        createDatabase(
            DriverFactory(this)
        )

        AccountsProvider(this).provide()
        accounts.startWatchingAccounts(mainLooper)

        SoundPlayer.setCacheDirectory(cacheDir)
    }

    override fun onTerminate() {
        super.onTerminate()

        accounts.stopWatchingAccounts()

        // Remove all viewing states
        settings.remove(SettingsKeys.SYS_VIEWING_EVENT)
    }
}
