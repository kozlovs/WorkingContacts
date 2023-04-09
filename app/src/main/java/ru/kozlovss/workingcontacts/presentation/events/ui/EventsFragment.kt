package ru.kozlovss.workingcontacts.presentation.events.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.databinding.FragmentEventsBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.events.adapter.EventLoadingStateAdapter
import ru.kozlovss.workingcontacts.presentation.events.adapter.EventsAdapter
import ru.kozlovss.workingcontacts.presentation.events.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.events.viewmodel.EventsViewModel
import ru.kozlovss.workingcontacts.presentation.event.ui.EventFragment.Companion.id
import ru.kozlovss.workingcontacts.presentation.newevent.ui.NewEventFragment.Companion.eventId
import ru.kozlovss.workingcontacts.presentation.userswall.ui.UserWallFragment.Companion.userId
import ru.kozlovss.workingcontacts.presentation.video.ui.VideoFragment.Companion.url

@AndroidEntryPoint
class EventsFragment : Fragment() {
    private val viewModel: EventsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = EventsAdapter(object : OnInteractionListener {
            override fun onLike(event: Event) {
                if (userViewModel.isLogin()) {
                    viewModel.likeById(event.id)
                } else DialogManager.errorAuthDialog(this@EventsFragment)
            }

            override fun onParticipate(event: Event) {
                if (userViewModel.isLogin()) {
                    viewModel.participateById(event.id)
                } else DialogManager.errorAuthDialog(this@EventsFragment)
            }

            override fun onShare(event: Event) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_event))
                startActivity(shareIntent)
            }

            override fun onRemove(event: Event) {
                if (userViewModel.isLogin()) {
                    viewModel.removeById(event.id)
                } else DialogManager.errorAuthDialog(this@EventsFragment)
            }

            override fun onEdit(event: Event) {
                if (userViewModel.isLogin()) {
                    findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment,
                        Bundle().apply { eventId = event.id })
                } else DialogManager.errorAuthDialog(this@EventsFragment)
            }

            override fun onToVideo(event: Event) {
                event.attachment?.let {
                    findNavController().navigate(R.id.action_global_videoFragment,
                    Bundle().apply { url = it.url  })
                }
            }

            override fun onSwitchAudio(event: Event) {
                viewModel.switchAudio(event)
            }

            override fun onToEvent(event: Event) {
                findNavController().navigate(
                    R.id.action_eventsFragment_to_eventFragment,
                    Bundle().apply { id = event.id })
            }

            override fun onToUser(event: Event) {
                findNavController().navigate(
                    R.id.action_eventsFragment_to_userWallFragment,
                    Bundle().apply { userId = event.authorId }
                )
            }
        })
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = EventLoadingStateAdapter { adapter.retry() },
            footer = EventLoadingStateAdapter { adapter.retry() }
        )

        subscribe(binding, adapter)
        setListeners(binding, adapter)

        return binding.root
    }

    private fun subscribe(binding: FragmentEventsBinding, adapter: EventsAdapter) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest {
                    binding.swipeRefresh.isRefreshing =
                        it.refresh is LoadState.Loading
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.token.collect {
                    adapter.refresh()
                }
            }
        }
    }

    private fun setListeners(binding: FragmentEventsBinding, adapter: EventsAdapter) {
        binding.add.setOnClickListener {
            if (userViewModel.isLogin()) {
                findNavController().navigate(R.id.action_eventsFragment_to_newEventFragment)
            } else DialogManager.errorAuthDialog(this)
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }
}