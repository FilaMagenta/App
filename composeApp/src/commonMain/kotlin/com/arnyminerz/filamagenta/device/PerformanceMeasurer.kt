package com.arnyminerz.filamagenta.device

interface PerformanceMeasurer {
    fun <Result> invoke(
        name: String,
        operation: String,
        metadata: Map<String, Any> = emptyMap(),
        measurements: Map<String, Number> = emptyMap(),
        block: MeasurementScope.() -> Result
    ): Result

    suspend fun <Result> suspending(
        name: String,
        operation: String,
        metadata: Map<String, Any> = emptyMap(),
        measurements: Map<String, Number> = emptyMap(),
        block: suspend MeasurementScope.() -> Result
    ): Result
}
