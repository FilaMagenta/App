package com.arnyminerz.filamagenta.device

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun CoroutineScope.launchMeasuring(
    name: String,
    operation: String,
    metadata: Map<String, Any> = emptyMap(),
    measurements: Map<String, Number> = emptyMap(),
    context: CoroutineContext = Dispatchers.IO,
    block: suspend MeasurementScope.() -> Unit
): Job {
    return launch(context) {
        Diagnostics.performance.suspending(
            name, operation, metadata, measurements, block
        )
    }
}
