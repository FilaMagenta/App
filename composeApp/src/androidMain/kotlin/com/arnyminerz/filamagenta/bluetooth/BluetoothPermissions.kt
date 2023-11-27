package com.arnyminerz.filamagenta.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.arnyminerz.filamagenta.android.MainActivity
import com.arnyminerz.filamagenta.android.applicationContext
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual object BluetoothPermissions {
    private val btManager: BluetoothManager by lazy {
        applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val _isScanning = MutableStateFlow(false)
    actual val isScanning: StateFlow<Boolean> get() = _isScanning

    // FIXME location access should not be needed for Android S+, but BlueFalcon still requires it
    private val permissions: List<String>
        get() {
            val base = mutableListOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                base.add(Manifest.permission.BLUETOOTH_ADVERTISE)
                base.add(Manifest.permission.BLUETOOTH_CONNECT)
                base.add(Manifest.permission.BLUETOOTH_SCAN)
            }

            return base
        }

    private fun permissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    actual fun arePermissionGranted(): Boolean {
        return permissions.all { permissionGranted(it) }
    }

    actual suspend fun requestPermissions() {
        checkNotNull(MainActivity.instance)

        Napier.d("Requesting BLE permission...")
        MainActivity.instance?.requestPermissions(permissions)
    }

    actual fun isBluetoothEnabled(): Boolean {
        return btManager.adapter.isEnabled
    }

    actual suspend fun enableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        MainActivity.instance?.launchForActivityResult(enableBtIntent)
    }

    @SuppressLint("MissingPermission")
    actual fun scanForDevices() {
        val btScanner = btManager.adapter.bluetoothLeScanner
        val callback = object: ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Napier.i { "Found BLE device: ${result.device.name}" }
            }

            override fun onScanFailed(errorCode: Int) {
                Napier.e { "Bluetooth scan failed. Code: $errorCode" }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                Napier.i { "Found ${results.size} BLE device(s): ${results.joinToString { it.device.name }}" }
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            Napier.i { "Stop scanning for BLE devices." }
            btScanner.stopScan(callback)
            _isScanning.value = false
        }, 30_000)

        Napier.i { "Start scanning for BLE devices." }
        btScanner.startScan(callback)
        _isScanning.value = true
    }
}
