package com.arnyminerz.filamagenta.diagnostics

import com.arnyminerz.filamagenta.BuildKonfig

object SentryInformation {
    val SentryDsn = BuildKonfig.SentryDsn

    val ReleaseName = BuildKonfig.ReleaseName

    val IsProduction = BuildKonfig.IsProduction
}
