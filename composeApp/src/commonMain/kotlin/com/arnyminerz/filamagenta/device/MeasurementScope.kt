package com.arnyminerz.filamagenta.device

interface MeasurementScope {
    companion object {
        val Empty = object : MeasurementScope {
            override fun setMetadata(key: String, value: Any) { }

            override fun setMeasurement(key: String, value: Number) { }
        }
    }

    fun setMetadata(key: String, value: Any)

    fun setMeasurement(key: String, value: Number)
}
