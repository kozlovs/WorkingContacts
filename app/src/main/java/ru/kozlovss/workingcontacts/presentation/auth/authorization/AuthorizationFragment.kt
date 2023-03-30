package ru.kozlovss.workingcontacts.presentation.auth.authorization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentAuthorizationBinding
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel

@AndroidEntryPoint
class AuthorizationFragment : Fragment() {

    private lateinit var binding: FragmentAuthorizationBinding
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizationBinding.inflate(inflater, container, false)

        setListeners()
        subscribe()
        return binding.root
    }

    private fun setListeners() {
        with(binding) {
            logInButton.setOnClickListener {
                val login = login.text.toString()
                val password = password.text.toString()
                viewModel.logIn(login, password)
            }
        }
    }


    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.token.collect {
                    it?.let {
                        findNavController().navigate(R.id.action_authorizationFragment_to_feedFragment)
                    }
                }
            }
        }
    }
}