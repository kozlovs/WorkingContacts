package ru.kozlovss.workingcontacts.presentation.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentStartBinding

@AndroidEntryPoint
class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        with(binding) {
            authorizationButton.setOnClickListener {
                findNavController().navigate(R.id.action_global_authorizationFragment)
            }
            registrationButton.setOnClickListener {
                findNavController().navigate(R.id.action_global_registrationFragment)
            }
            skipButton.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_feedFragment)
            }
        }
    }
}