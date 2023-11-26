package com.arnyminerz.filamagenta.bluetooth

expect object BluetoothPermissions {
    fun arePermissionGranted(): Boolean

    suspend fun requestPermissions()
}
