package ru.kozlovss.workingcontacts.presentation.auth.authorization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentAuthorizationBinding
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel

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
            loginButton.setOnClickListener {
                val login = login.text.toString()
                val password = password.text.toString()
                viewModel.logIn(login, password)
            }
            registrationButton.setOnClickListener {
                findNavController().navigate(R.id.action_authorizationFragment_to_registrationFragment)
            }
        }
    }


    private fun subscribe() {
        lifecycleScope.launchWhenCreated {
            viewModel.token.collect {
                it?.let {
                    viewModel.saveTokenOfUser(it.id, it.token)
                    findNavController().navigateUp()//todo на главный экран
                }
            }
        }
    }
}