package ru.kozlovss.workingcontacts.presentation.auth.ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentRegistrationBinding
import ru.kozlovss.workingcontacts.presentation.auth.model.AuthModel
import ru.kozlovss.workingcontacts.presentation.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.util.PermissionManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.AuthViewModel

class RegistrationFragment : Fragment() {

    private var binding: FragmentRegistrationBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.photo_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Activity.RESULT_OK -> {
                    val uri = it.data?.data
                    viewModel.saveAvatar(uri, uri?.toFile())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        setListeners()
        subscribe()
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setListeners() = with(binding!!) {
        signInButton.setOnClickListener {
            if (isPasswordsDifferent()) {
                DialogManager.differentPasswordsDialog(this@RegistrationFragment)
                return@setOnClickListener
            }
            val name = name.text.toString()
            val login = login.text.toString()
            val password = password.text.toString()
            try {
                viewModel.register(login, password, name)
            } catch (e: Exception) {
                DialogManager.errorDialog(this@RegistrationFragment, e)
            }
        }

        avatar.setOnClickListener {
            DialogManager.addPhotoDialog(
                this@RegistrationFragment,
                {
                    if (PermissionManager.checkImagePermission(requireActivity())) {
                        ImagePicker.Builder(this@RegistrationFragment)
                            .cameraOnly()
                            .crop()
                            .maxResultSize(2048, 2048)
                            .createIntent(imageLauncher::launch)
                    } else {
                        PermissionManager.requestImagePermission(requireActivity())
                    }
                },
                {
                    if (PermissionManager.checkImagePermission(requireActivity())) {
                        ImagePicker.Builder(this@RegistrationFragment)
                            .galleryOnly()
                            .crop()
                            .maxResultSize(2048, 2048)
                            .createIntent(imageLauncher::launch)
                    } else {
                        PermissionManager.requestImagePermission(requireActivity())
                    }
                }
            )
        }

        removeAvatarButton.setOnClickListener {
            viewModel.clearAvatar()
        }
    }

    private fun isPasswordsDifferent() = with(binding!!) {
        password.text.toString() != rePassword.text.toString()
    }

    private fun subscribe() = with(binding!!) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.token.collect {
                    it?.let {
                        findNavController().navigate(R.id.action_global_feedFragment)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.avatar.collect {
                    if (it == null) {
                        avatar.setImageResource(R.drawable.baseline_person_outline_24)
                    } else {
                        avatar.setImageURI(it.uri)
                    }
                    removeAvatarButton.isVisible = it != null
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    registrationCard.isVisible = state is AuthModel.State.Idle
                    progress.isVisible = state is AuthModel.State.Loading
                }
            }
        }
    }
}