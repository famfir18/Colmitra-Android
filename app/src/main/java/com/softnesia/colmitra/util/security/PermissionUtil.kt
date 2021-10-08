package com.softnesia.colmitra.util.security

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Utility class that wraps access to the runtime permissions API in M and provides basic helper
 * methods.
 */
object PermissionUtil {
    const val REQUEST_EXTERNAL_STORAGE = 1
    const val REQUEST_CAMERA = 2
    const val REQUEST_LOCATION = 3

    var PERMISSIONS_CAMERA = arrayOf(
        Manifest.permission.CAMERA
    )
    var PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var PERMISSIONS_LOCATION = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    /**
     * Checks if the app has permission to write to device storage
     *
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyStoragePermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            PERMISSIONS_STORAGE,
            REQUEST_EXTERNAL_STORAGE
        )
    }

    fun verifyCameraPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            PERMISSIONS_CAMERA,
            REQUEST_CAMERA
        )
    }

    fun verifyLocationPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            PERMISSIONS_LOCATION,
            REQUEST_LOCATION
        )
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value [PackageManager.PERMISSION_GRANTED].
     */
    fun isPermissionsGranted(grantResults: IntArray): Boolean {
        // At least one result must be checked.
        if (grantResults.isEmpty()) {
            return false
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }
}