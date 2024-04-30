package com.example.camerademo

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

internal class AppPermissions(
    private val activity: FragmentActivity,
    private val onPermissionGranted: () -> Unit,
) {

    private val requiredPermissions = buildList {
        add(android.Manifest.permission.CAMERA)
        add(android.Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()
    private val permission = activity.registerForActivityResult(RequestMultiplePermissions()) {
        onPermissionGranted()
    }

    private fun requestPermissions() = permission.launch(requiredPermissions)

    private fun allPermissionsGranted() = requiredPermissions.all { permission ->
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun run() {
        if (allPermissionsGranted()) {
            onPermissionGranted()
        } else {
            requestPermissions()
        }
    }
}

