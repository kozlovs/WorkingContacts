package ru.kozlovss.workingcontacts.domain.util

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.kozlovss.workingcontacts.R

object DialogManager {

    fun errorDialog(fragment: Fragment, e: Exception) =
        MaterialAlertDialogBuilder(fragment.requireContext())
            .create()
            .apply {
                setTitle("Error")
                setMessage(makeErrorMessage(e))
                setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                    dismiss()
                }
            }
            .show()

    fun differentPasswordsDialog(fragment: Fragment) =
        MaterialAlertDialogBuilder(fragment.requireContext())
            .create()
            .apply {
                setTitle("Error")
                setMessage("Passwords are different")
                setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                    dismiss()
                }
            }
            .show()

    fun errorAuthDialog(fragment: Fragment) =
        MaterialAlertDialogBuilder(fragment.requireContext())
            .create()
            .apply {
                setTitle("Auth error")
                setMessage("Sign In to yur account")
                setButton(AlertDialog.BUTTON_POSITIVE, "Sign in") { _, _ ->
                    fragment.findNavController().navigate(R.id.action_global_authorizationFragment)
                }
                setButton(AlertDialog.BUTTON_NEUTRAL, "Sign up") { _, _ ->
                    fragment.findNavController().navigate(R.id.action_global_registrationFragment)
                }
                setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
                    dismiss()
                }
            }.show()

    fun addPhotoDialog(fragment: Fragment, onToCamera: () -> Unit, onToGallery: () -> Unit) =
        MaterialAlertDialogBuilder(fragment.requireContext())
            .create()
            .apply {
                setTitle("User's photo upload")
                setMessage("Add your photo")
                setButton(AlertDialog.BUTTON_POSITIVE, "Camera") { _, _ ->
                    onToCamera()
                }
                setButton(AlertDialog.BUTTON_NEUTRAL, "Gallery") { _, _ ->
                    onToGallery()
                }
                setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
                    dismiss()
                }
            }.show()

    //        takePhoto.setOnClickListener {
//            ImagePicker.Builder(this@RegistrationFragment)
//                .cameraOnly()
//                .maxResultSize(2048, 2048)
//                .createIntent(imageLauncher::launch)
//        }
//
//        gallery.setOnClickListener {
//            ImagePicker.Builder(this@RegistrationFragment)
//                .galleryOnly()
//                .maxResultSize(2048, 2048)
//                .createIntent(imageLauncher::launch)
//        }
//
//        clear.setOnClickListener {
//            viewModel.clearAvatar()
//        }//todo добавить механизм загрузки фото
    private fun makeErrorMessage(e: Exception) = e.message.toString()
}