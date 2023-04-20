package ru.kozlovss.workingcontacts.presentation.auth.ui

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

    private var binding: FragmentStartBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        setListeners()
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setListeners() = with(binding!!) {
        authorizationButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_authorizationFragment)
        }
        registrationButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_registrationFragment)
        }
        skipButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_feedFragment)
        }
    }
}