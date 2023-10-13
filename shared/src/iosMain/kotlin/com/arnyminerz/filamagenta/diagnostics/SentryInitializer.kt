package com.arnyminerz.filamagenta.diagnostics

import com.arnyminerz.filamagenta.BuildKonfig
import io.sentry.kotlin.multiplatform.Sentry

actual class SentryInitializer {
    actual fun init() {
        Sentry.init { options ->
            options.dsn = BuildKonfig.SentryDsn
            options.release = BuildKonfig.ReleaseName
            options.debug = !BuildKonfig.IsProduction
            options.dist = BuildKonfig.ReleaseName.substringBefore('-')
            options.environment = if (BuildKonfig.IsProduction) "prod" else "dev"
        }
    }
}
