package ru.kozlovss.workingcontacts.presentation.auth.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentAuthorizationBinding
import ru.kozlovss.workingcontacts.presentation.auth.model.AuthModel
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.AuthViewModel

@AndroidEntryPoint
class AuthorizationFragment : Fragment() {

    private var binding: FragmentAuthorizationBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizationBinding.inflate(inflater, container, false)

        setListeners()
        subscribe()
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setListeners() = with(binding!!) {
        logInButton.setOnClickListener {
            val login = login.text.toString()
            val password = password.text.toString()
            viewModel.logIn(login, password)
        }
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
                viewModel.state.collect { state ->
                    authorizationCard.isVisible = state is AuthModel.State.Idle
                    progress.isVisible = state is AuthModel.State.Loading
                }
            }
        }
    }
}