package ru.kozlovss.workingcontacts.presentation.userswall.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.databinding.FragmentJobsListBinding
import ru.kozlovss.workingcontacts.presentation.feed.model.FeedModel
import ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs.JobsAdapter
import ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.userswall.ui.UserWallFragment.Companion.userId
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

    private fun subscribe() {
        lifecycleScope.launchWhenStarted {
            viewModel.jobsData.collect {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.state.collectLatest { state ->
                binding.swipeRefresh.isRefreshing = state is FeedModel.FeedModelState.Refreshing
                if (state is FeedModel.FeedModelState.Error) {
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.retry_loading) {
                            viewModel.getPosts(arguments?.userId!!)
                        }
                        .show()
                }
            }
        }
    }

    private fun init() = with(binding) {
        list.layoutManager = LinearLayoutManager(activity)
        adapter = JobsAdapter(object : OnInteractionListener {
            override fun onRemove(job: Job) {
                TODO("Not yet implemented")
            }

            override fun onEdit(job: Job) {
                TODO("Not yet implemented")
            }

        })
        list.adapter = adapter
    }

    private fun setListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getPosts(arguments?.userId!!)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = JobsListFragment()
    }
}