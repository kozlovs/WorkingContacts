package ru.kozlovss.workingcontacts.domain.util

import android.app.AlertDialog
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.presentation.events.ui.EventFragment
import ru.kozlovss.workingcontacts.presentation.events.ui.EventsFragment
import ru.kozlovss.workingcontacts.presentation.events.ui.NewEventFragment
import ru.kozlovss.workingcontacts.presentation.feed.ui.FeedFragment
import ru.kozlovss.workingcontacts.presentation.feed.ui.NewPostFragment
import ru.kozlovss.workingcontacts.presentation.feed.ui.PostFragment

object DialogManager {

    fun errorDialog(fragment: Fragment, e: Exception) {
        val builder = MaterialAlertDialogBuilder(fragment.requireContext())
        val dialog = builder.create()
        dialog.setTitle("Error")
        val massage = makeErrorMessage(e)
        dialog.setMessage(massage)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun differentPasswordsDialog(fragment: Fragment) {
        val builder = MaterialAlertDialogBuilder(fragment.requireContext())
        val dialog = builder.create()
        dialog.setTitle("Error")
        dialog.setMessage("Passwords are different")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun errorAuthDialog(fragment: Fragment) {
        val builder = MaterialAlertDialogBuilder(fragment.requireContext())
        val dialog = builder.create()
        dialog.setTitle("Auth error")
        dialog.setMessage("Sign In to yur account")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sign in") { _, _ ->
            when (fragment) {
                is FeedFragment -> fragment.findNavController().navigate(R.id.action_feedFragment_to_authorizationFragment)
                is PostFragment -> fragment.findNavController().navigate(R.id.action_postFragment_to_authorizationFragment)
                is NewPostFragment -> fragment.findNavController().navigate(R.id.action_newPostFragment_to_authorizationFragment)
                is EventsFragment -> fragment.findNavController().navigate(R.id.action_eventsFragment_to_authorizationFragment)
                is EventFragment -> fragment.findNavController().navigate(R.id.action_eventFragment_to_authorizationFragment)
                is NewEventFragment -> fragment.findNavController().navigate(R.id.action_newEventFragment_to_authorizationFragment)
            }
        }
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Sign up") { _, _ ->
            when (fragment) {
                is FeedFragment -> fragment.findNavController().navigate(R.id.action_feedFragment_to_registrationFragment)
                is PostFragment -> fragment.findNavController().navigate(R.id.action_postFragment_to_registrationFragment)
                is NewPostFragment -> fragment.findNavController().navigate(R.id.action_newPostFragment_to_registrationFragment)
                is EventsFragment -> fragment.findNavController().navigate(R.id.action_eventsFragment_to_registrationFragment)
                is EventFragment -> fragment.findNavController().navigate(R.id.action_eventFragment_to_registrationFragment)
                is NewEventFragment -> fragment.findNavController().navigate(R.id.action_newEventFragment_to_registrationFragment)
            }
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun addPhotoDialog(fragment: Fragment, isPhotoUploaded: Boolean = false) {
        val builder = MaterialAlertDialogBuilder(fragment.requireContext())
        val dialog = builder.create()
        dialog.setTitle("Photo upload")
        if (isPhotoUploaded) {
            dialog.setMessage("Manage your photo")
        } else {
            dialog.setMessage("Add your photo")
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
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
    }

    private fun makeErrorMessage(e: Exception): String {
        return e.message.toString()
    }
}