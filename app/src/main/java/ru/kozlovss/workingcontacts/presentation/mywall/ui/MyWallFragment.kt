package ru.kozlovss.workingcontacts.presentation.mywall.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentMyWallBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.mywall.viewmodel.MyWallViewModel
import ru.kozlovss.workingcontacts.presentation.mywall.model.MyWallModel
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.vp.VpAdapter

@AndroidEntryPoint
class MyWallFragment : Fragment() {

    private val myWallViewModel: MyWallViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentMyWallBinding
    private val fragmentsList = listOf(
        MyPostsListFragment.newInstance(),
        MyJobsListFragment.newInstance()
    )
    private val tabList = listOf("Posts", "Jobs")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyWallBinding.inflate(inflater, container, false)
        init()
        subscribe()
        setListeners()

        return binding.root
    }

    private fun init() = with(binding) {
        val adapter = VpAdapter(activity as FragmentActivity, fragmentsList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()
    }

    private fun subscribe() {
        lifecycleScope.launchWhenStarted {
            myWallViewModel.myData.collect {
                it?.let {
                    binding.name.text = it.name
                    if (it.avatar != null) {
                        Glide.with(binding.avatar)
                            .load(it.avatar)
                            .placeholder(R.drawable.baseline_update_24)
                            .error(R.drawable.baseline_error_outline_24)
                            .timeout(10_000)
                            .into(binding.avatar)
                    } else {
                        binding.avatar.setImageResource(R.drawable.baseline_person_outline_24)
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            myWallViewModel.state.collectLatest { state ->
                binding.authButtons.isVisible = state is MyWallModel.State.NoLogin
                binding.progress.isVisible = state is MyWallModel.State.Loading
                binding.myCard.isVisible = state is MyWallModel.State.Idle
                binding.add.isVisible = state is MyWallModel.State.Idle
            }
        }

        myWallViewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id == 0L) return@observe
            findNavController().navigate(R.id.action_myWallFragment_to_newPostFragment)
        }

        lifecycleScope.launchWhenCreated {
            userViewModel.token.collect { token ->
                myWallViewModel.updateMyData(token)
            }
        }
    }

    private fun setListeners() = with(binding) {
        add.setOnClickListener {
            if (myWallViewModel.isLogin()) findNavController().navigate(R.id.action_myWallFragment_to_newPostFragment)
            else DialogManager.errorAuthDialog(this@MyWallFragment)
        }

        buttonLogout.setOnClickListener {
            userViewModel.logout()
        }

        onLogInButton.setOnClickListener {
            findNavController().navigate(R.id.action_myWallFragment_to_authorizationFragment)
        }

        onSignInButton.setOnClickListener {
            findNavController().navigate(R.id.action_myWallFragment_to_registrationFragment)
        }
    }
}