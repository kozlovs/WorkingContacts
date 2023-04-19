package ru.kozlovss.workingcontacts.presentation.userswall.ui

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
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.databinding.FragmentUserWallBinding
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.presentation.userswall.adapter.vp.VpAdapter
import ru.kozlovss.workingcontacts.presentation.userswall.model.UserWallModel
import ru.kozlovss.workingcontacts.presentation.userswall.viewmodel.UserWallViewModel


@AndroidEntryPoint
class UserWallFragment : Fragment() {
    private val viewModel: UserWallViewModel by activityViewModels()
    private lateinit var binding: FragmentUserWallBinding
    private val fragmentsList = listOf(
        PostsListFragment.newInstance(),
        JobsListFragment.newInstance()
    )
    private val tabList = listOf(getString(R.string.posts), getString(R.string.jobs))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserWallBinding.inflate(inflater, container, false)
        init()
        subscribe()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearData()
    }

    private fun subscribe() = with(binding) {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userData.collect {
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
                viewModel.state.collectLatest { state ->
                    progress.isVisible = state is UserWallModel.State.Loading
                    wallLayout.isVisible =
                        (state is UserWallModel.State.Idle) || (state is UserWallModel.State.RefreshingJobs) || (state is UserWallModel.State.RefreshingPosts)
                    errorLayout.isVisible = state is UserWallModel.State.Error
                }
            }
        }
    }

    private fun init() = with(binding) {
        viewModel.getData(arguments?.userId!!)
        val adapter = VpAdapter(activity as FragmentActivity, fragmentsList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()
    }

    companion object {
        var Bundle.userId: Long by LongArg
    }
}