package com.arnyminerz.filamagenta.device

object Diagnostics {

    var updateUserInformation: ((username: String, email: String) -> Unit)? = null

    var deleteUserInformation: (() -> Unit)? = null

    var performance: PerformanceMeasurer = object : PerformanceMeasurer {
        override operator fun <Result> invoke(
            name: String,
            operation: String,
            metadata: Map<String, Any>,
            measurements: Map<String, Number>,
            block: MeasurementScope.() -> Result
        ): Result {
            return block(MeasurementScope.Empty)
        }

        override suspend fun <Result> suspending(
            name: String,
            operation: String,
            metadata: Map<String, Any>,
            measurements: Map<String, Number>,
            block: suspend MeasurementScope.() -> Result
        ): Result {
            return block(MeasurementScope.Empty)
        }
    }
}
