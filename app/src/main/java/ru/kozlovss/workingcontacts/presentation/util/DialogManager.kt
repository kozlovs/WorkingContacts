package ru.kozlovss.workingcontacts.presentation.util

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
                setTitle(R.string.error)
                setMessage(makeErrorMessage(e))
                setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)) { _, _ ->
                    dismiss()
                }
            }
            .show()

    fun differentPasswordsDialog(fragment: Fragment) =
        MaterialAlertDialogBuilder(fragment.requireContext())
            .create()
            .apply {
                setTitle(R.string.error)
                setMessage(context.getString(R.string.passwords_are_different))
                setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)) { _, _ ->
                    dismiss()
                }
            }
            .show()

    fun errorAuthDialog(fragment: Fragment) =
        MaterialAlertDialogBuilder(fragment.requireContext())
            .create()
            .apply {
                setTitle(R.string.auth_error)
                setMessage(context.getString(R.string.sign_in_to_your_account))
                setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.Sign_in)) { _, _ ->
                    fragment.findNavController().navigate(R.id.action_global_authorizationFragment)
                }
                setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.Sign_up)) { _, _ ->
                    fragment.findNavController().navigate(R.id.action_global_registrationFragment)
                }
                setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.Cancel)) { _, _ ->
                    dismiss()
                }
            }.show()

    fun addPhotoDialog(fragment: Fragment, onToCamera: () -> Unit, onToGallery: () -> Unit) =
        MaterialAlertDialogBuilder(fragment.requireContext())
            .create()
            .apply {
                setTitle(R.string.user_s_photo_upload)
                setMessage(context.getString(R.string.add_your_photo))
                setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.camera)) { _, _ ->
                    onToCamera()
                }
                setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.gallery)) { _, _ ->
                    onToGallery()
                }
                setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.Cancel)) { _, _ ->
                    dismiss()
                }
            }.show()
    private fun makeErrorMessage(e: Exception) = e.message.toString()
}