package ru.kozlovss.workingcontacts.presentation.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

object PermissionManager {

    private var storage_permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private var image_permissions_33 = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)

    private fun imagePermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) image_permissions_33
        else storage_permissions

    fun checkImagePermission(activity: Activity) = imagePermissions()
        .map { activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        .contains(false)

    fun requestImagePermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, imagePermissions(), 1)
    }
}