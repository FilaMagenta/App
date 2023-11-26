package com.arnyminerz.filamagenta.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.arnyminerz.filamagenta.android.MainActivity
import com.arnyminerz.filamagenta.android.applicationContext
import io.github.aakira.napier.Napier

actual object BluetoothPermissions {
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
}
