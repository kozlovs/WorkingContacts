package ru.kozlovss.workingcontacts.presentation.mywall.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentMyWallBinding
import ru.kozlovss.workingcontacts.presentation.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.mywall.viewmodel.MyWallViewModel
import ru.kozlovss.workingcontacts.presentation.mywall.model.MyWallModel
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.vp.VpAdapter

@AndroidEntryPoint
class MyWallFragment : Fragment() {

    private val myWallViewModel: MyWallViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var binding: FragmentMyWallBinding? = null
    private var fragmentsList: List<Fragment>? = null
    private var tabList: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyWallBinding.inflate(inflater, container, false)
        init()
        subscribe()
        setListeners()

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        tabList = null
        fragmentsList = null
    }

    private fun init() = with(binding!!) {
        fragmentsList = listOf(
            MyPostsListFragment.newInstance(),
            MyJobsListFragment.newInstance()
        )
        tabList = listOf(getString(R.string.posts), getString(R.string.jobs))
        val adapter = VpAdapter(activity as FragmentActivity, fragmentsList!!)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, pos ->
            tab.text = tabList!![pos]
        }.attach()
    }

    private fun subscribe() = with(binding!!) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myWallViewModel.myData.collect {
                    it?.let {
                        name.text = it.name
                        if (it.avatar != null) {
                            Glide.with(avatar)
                                .load(it.avatar)
                                .placeholder(R.drawable.baseline_update_24)
                                .error(R.drawable.baseline_error_outline_24)
                                .timeout(10_000)
                                .into(avatar)
                        } else {
                            avatar.setImageResource(R.drawable.baseline_person_outline_24)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myWallViewModel.state.collectLatest { state ->
                    authButtons.isVisible = state is MyWallModel.State.NoLogin
                    progress.isVisible = state is MyWallModel.State.Loading
                    wallLayout.isVisible =
                        state is MyWallModel.State.Idle || state is MyWallModel.State.RefreshingJobs
                    add.isVisible = state is MyWallModel.State.Idle
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.token.collect { token ->
                    myWallViewModel.updateMyData(token)
                }
            }
        }
    }

    private fun setListeners() = with(binding!!) {
        add.setOnClickListener {
            if (myWallViewModel.isLogin()) {
                if (tabLayout.selectedTabPosition == 0) findNavController().navigate(R.id.action_global_newPostFragment)
                else findNavController().navigate(R.id.action_myWallFragment_to_newJobFragment)
            }
            else DialogManager.errorAuthDialog(this@MyWallFragment)
        }

        buttonLogout.setOnClickListener {
            userViewModel.logout()
        }

        onLogInButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_authorizationFragment)
        }

        onSignInButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_registrationFragment)
        }
    }
}