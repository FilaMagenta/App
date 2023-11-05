package com.arnyminerz.filamagenta.diagnostics

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import io.github.aakira.napier.Napier
import io.sentry.kotlin.multiplatform.SentryOptions

object SentryDiagnostics {
    fun initialize(platformInitializer: (configure: (options: SentryOptions) -> Unit) -> Unit) {
        if (settings.getBoolean(SettingsKeys.DATA_COLLECTION, true)) {
            if (BuildKonfig.SentryDsn.isBlank()) {
                Napier.w { "Sentry should be enabled, but its DSN is empty." }
                return
            }

            platformInitializer { options ->
                options.dsn = BuildKonfig.SentryDsn
                options.release = BuildKonfig.ReleaseName
                options.dist = BuildKonfig.ReleaseName.substringBefore('-')
                options.environment = if (BuildKonfig.IsProduction) "prod" else "dev"
            }
            Napier.i { "Initialized Sentry" }
        }
    }
}
