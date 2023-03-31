package ru.kozlovss.workingcontacts.domain.util

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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private var video_permissions_33 = arrayOf(Manifest.permission.READ_MEDIA_VIDEO)

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private var audio_permissions_33 = arrayOf(Manifest.permission.READ_MEDIA_AUDIO)


    private fun imagePermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) image_permissions_33
        else storage_permissions

    private fun videoPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) video_permissions_33
        else storage_permissions

    private fun audioPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) audio_permissions_33
        else storage_permissions

    fun checkImagePermission(activity: Activity) = imagePermissions()
        .map { activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        .contains(false)

    fun checkVideoPermission(activity: Activity) = videoPermissions()
        .map { activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        .contains(false)

    fun checkAudioPermission(activity: Activity) = audioPermissions()
        .map { activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        .contains(false)

    fun requestImagePermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, imagePermissions(), 1)
    }

    fun requestVideoPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, videoPermissions(), 1)
    }

    fun requestAudioPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, audioPermissions(), 1)
    }
}