package ru.kozlovss.workingcontacts.presentation.userswall.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.databinding.FragmentJobsListBinding
import ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs.JobsAdapter
import ru.kozlovss.workingcontacts.presentation.userswall.model.UserWallModel
import ru.kozlovss.workingcontacts.presentation.userswall.viewmodel.UserWallViewModel

@AndroidEntryPoint
class JobsListFragment : Fragment() {

    private lateinit var binding: FragmentJobsListBinding
    private val viewModel: UserWallViewModel by activityViewModels()
    private lateinit var adapter: JobsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        subscribe()
        setListeners()
    }

    private fun subscribe() = with(binding) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.jobsData.collect {
                    adapter.submitList(it)
                    empty.isVisible = it.isEmpty()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    swipeRefresh.isRefreshing = state is UserWallModel.State.RefreshingJobs
                }
            }
        }
    }

    private fun init() = with(binding) {
        list.layoutManager = LinearLayoutManager(activity)
        adapter = JobsAdapter()
        list.adapter = adapter
    }

    private fun setListeners() = with(binding) {
        swipeRefresh.setOnRefreshListener {
            viewModel.getJobs()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = JobsListFragment()
    }
}