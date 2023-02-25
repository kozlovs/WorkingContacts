package ru.kozlovss.workingcontacts.presentation.auth.registration

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentRegistrationBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private val viewModel: UserViewModel by activityViewModels()

//    private val imageLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            when (it.resultCode) {
//                ImagePicker.RESULT_ERROR -> {
//                    Toast.makeText(
//                        requireContext(),
//                        getString(R.string.photo_error),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                Activity.RESULT_OK -> {
//                    val uri = it.data?.data
//                    viewModel.saveAvatar(uri, uri?.toFile())
//                }
//            }
//        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        setListeners()
        subscribe()
        return binding.root
    }

    private fun setListeners() = with(binding) {
        registerButton.setOnClickListener {
            if (isPasswordsDifferent()) {
                DialogManager.differentPasswordsDialog(this@RegistrationFragment)
                return@setOnClickListener
            }
            val name = name.text.toString()
            val login = login.text.toString()
            val password = password.text.toString()

            viewModel.register(login, password, name)
        }

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
//        }
    }

    private fun isPasswordsDifferent() = with(binding) {
        password.text.toString() != rePassword.text.toString()
    }

    private fun subscribe() {
        lifecycleScope.launchWhenCreated {
            viewModel.token.collect {
                it?.let {
                    viewModel.saveTokenOfUser(it.id, it.token)
                    findNavController().navigateUp()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.avatar.collect {
                with(binding) {
                    if (it == null) {
                        avatar.setImageResource(R.drawable.baseline_person_outline_24)
                    } else {
                        avatar.setImageURI(it.uri)
                    }
                }
            }
        }
    }
}