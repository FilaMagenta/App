package com.arnyminerz.filamagenta.bluetooth

import kotlinx.coroutines.flow.StateFlow

expect object BluetoothPermissions {
    val isScanning: StateFlow<Boolean>

    fun arePermissionGranted(): Boolean

    suspend fun requestPermissions()

    fun isBluetoothEnabled(): Boolean

    suspend fun enableBluetooth()

    fun scanForDevices()
}
