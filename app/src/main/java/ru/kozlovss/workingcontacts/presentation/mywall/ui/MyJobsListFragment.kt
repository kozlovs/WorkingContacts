package ru.kozlovss.workingcontacts.presentation.mywall.ui

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
import ru.kozlovss.workingcontacts.entity.Job
import ru.kozlovss.workingcontacts.databinding.FragmentMyJobsListBinding
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.AuthViewModel
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs.JobsAdapter
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.mywall.model.MyWallModel
import ru.kozlovss.workingcontacts.presentation.mywall.viewmodel.MyWallViewModel
import ru.kozlovss.workingcontacts.presentation.newjob.viewmodel.NewJobViewModel

@AndroidEntryPoint
class MyJobsListFragment : Fragment() {

    private var binding: FragmentMyJobsListBinding? = null
    private val viewModel: MyWallViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val newJobViewModel: NewJobViewModel by activityViewModels()
    private var adapter: JobsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyJobsListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        subscribe()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    private fun subscribe() = with(binding!!) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.jobsData.collectLatest {
                    adapter!!.submitList(it)
                    empty.isVisible = it.isEmpty()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    errorLayout.isVisible = state is MyWallModel.State.Error
                    swipeRefresh.isRefreshing = state is MyWallModel.State.RefreshingJobs
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.token.collect { token ->
                    token?.let { viewModel.getJobs() }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                newJobViewModel.events.collect {
                    when (it) {
                        NewJobViewModel.Event.CreateNewItem -> {
                            viewModel.getJobs()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun init() = with(binding!!) {
        list.layoutManager = LinearLayoutManager(activity)
        adapter = JobsAdapter(
            object : OnInteractionListener {
                override fun onRemove(job: Job) {
                    viewModel.removeJobById(job.id)
                }
            },
            requireContext()
        )

        list.adapter = adapter
    }

    private fun setListeners() = with(binding!!) {
        swipeRefresh.setOnRefreshListener {
            viewModel.getJobs()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyJobsListFragment()
    }
}