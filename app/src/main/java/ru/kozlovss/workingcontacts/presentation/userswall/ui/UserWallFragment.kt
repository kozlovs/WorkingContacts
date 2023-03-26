package ru.kozlovss.workingcontacts.presentation.userswall.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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
    private val tabList = listOf("Posts", "Jobs")

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

    private fun subscribe() {

        lifecycleScope.launchWhenCreated {
            viewModel.userData.collect {
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
            viewModel.state.collectLatest { state ->
                binding.progress.isVisible = state is UserWallModel.State.Loading
                binding.wallLayout.isVisible = (state is UserWallModel.State.Idle) || (state is UserWallModel.State.RefreshingJobs) || (state is UserWallModel.State.RefreshingPosts)
                binding.errorLayout.isVisible = state is UserWallModel.State.Error
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