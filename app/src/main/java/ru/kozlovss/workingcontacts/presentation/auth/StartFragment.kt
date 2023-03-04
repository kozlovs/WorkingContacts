package ru.kozlovss.workingcontacts.presentation.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentStartBinding
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel

@AndroidEntryPoint
class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        subscribe()
        setListeners()
        return binding.root
    }

    private fun subscribe() {
//        lifecycleScope.launchWhenCreated {
//            viewModel.token.collect { token ->
//                if (token != null) findNavController().navigate(R.id.action_startFragment_to_feedFragment)
//            }
//        }
    }

    private fun setListeners() {
        with(binding) {
            authorizationButton.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_authorizationFragment)
            }
            registrationButton.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_registrationFragment)
            }
        }
    }
}