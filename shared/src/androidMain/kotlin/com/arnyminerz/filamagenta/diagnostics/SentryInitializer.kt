package com.arnyminerz.filamagenta.diagnostics

import android.content.Context
import com.arnyminerz.filamagenta.BuildKonfig
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.Sentry

actual class SentryInitializer(private val context: Context) {
    actual fun init() {
        Sentry.init(context) { options ->
            options.dsn = BuildKonfig.SentryDsn
            options.release = BuildKonfig.ReleaseName
            options.debug = !BuildKonfig.IsProduction
            options.dist = BuildKonfig.ReleaseName.substringBefore('-')
            options.environment = if (BuildKonfig.IsProduction) "prod" else "dev"
        }
        SentryAndroid.init(context) { options ->
            options.dsn = BuildKonfig.SentryDsn
            options.release = BuildKonfig.ReleaseName
            options.dist = BuildKonfig.ReleaseName.substringBefore('-')
            options.environment = if (BuildKonfig.IsProduction) "prod" else "dev"
            options.tracesSampleRate = 1.0
        }
    }
}
