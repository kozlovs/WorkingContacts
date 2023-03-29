package ru.kozlovss.workingcontacts.domain.util

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.presentation.event.ui.EventFragment
import ru.kozlovss.workingcontacts.presentation.events.ui.EventsFragment
import ru.kozlovss.workingcontacts.presentation.newevent.ui.NewEventFragment
import ru.kozlovss.workingcontacts.presentation.feed.ui.FeedFragment
import ru.kozlovss.workingcontacts.presentation.newpost.ui.NewPostFragment
import ru.kozlovss.workingcontacts.presentation.post.ui.PostFragment

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
                    when (fragment) {
                        is FeedFragment -> fragment.findNavController()
                            .navigate(R.id.action_feedFragment_to_authorizationFragment)
                        is PostFragment -> fragment.findNavController()
                            .navigate(R.id.action_postFragment_to_authorizationFragment)
                        is NewPostFragment -> fragment.findNavController()
                            .navigate(R.id.action_newPostFragment_to_authorizationFragment)
                        is EventsFragment -> fragment.findNavController()
                            .navigate(R.id.action_eventsFragment_to_authorizationFragment)
                        is EventFragment -> fragment.findNavController()
                            .navigate(R.id.action_eventFragment_to_authorizationFragment)
                        is NewEventFragment -> fragment.findNavController()
                            .navigate(R.id.action_newEventFragment_to_authorizationFragment)
                    }
                }
                setButton(AlertDialog.BUTTON_NEUTRAL, "Sign up") { _, _ ->
                    when (fragment) {
                        is FeedFragment -> fragment.findNavController()
                            .navigate(R.id.action_feedFragment_to_registrationFragment)
                        is PostFragment -> fragment.findNavController()
                            .navigate(R.id.action_postFragment_to_registrationFragment)
                        is NewPostFragment -> fragment.findNavController()
                            .navigate(R.id.action_newPostFragment_to_registrationFragment)
                        is EventsFragment -> fragment.findNavController()
                            .navigate(R.id.action_eventsFragment_to_registrationFragment)
                        is EventFragment -> fragment.findNavController()
                            .navigate(R.id.action_eventFragment_to_registrationFragment)
                        is NewEventFragment -> fragment.findNavController()
                            .navigate(R.id.action_newEventFragment_to_registrationFragment)
                    }
                }
                setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
                    dismiss()
                }
            }.show()

    fun addPhotoDialog(fragment: Fragment, isPhotoUploaded: Boolean = false) =
        MaterialAlertDialogBuilder(fragment.requireContext())
            .create()
            .apply {
                setTitle("Photo upload")
                if (isPhotoUploaded) setMessage("Manage your photo")
                else setMessage("Add your photo")
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