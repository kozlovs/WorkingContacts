package ru.kozlovss.workingcontacts.presentation.feed.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.FragmentFeedBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.feed.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.feed.adapter.PostLoadingStateAdapter
import ru.kozlovss.workingcontacts.presentation.feed.adapter.PostsAdapter
import ru.kozlovss.workingcontacts.presentation.feed.viewmodel.PostViewModel
import ru.kozlovss.workingcontacts.presentation.feed.ui.PostFragment.Companion.id
import ru.kozlovss.workingcontacts.presentation.userswall.ui.UserWallFragment.Companion.userId
import ru.kozlovss.workingcontacts.presentation.video.VideoFragment.Companion.url

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (userViewModel.isLogin()) {
                    viewModel.likeById(post.id)
                } else DialogManager.errorAuthDialog(this@FeedFragment)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onRemove(post: Post) {
                if (userViewModel.isLogin()) {
                    viewModel.removeById(post.id)
                } else DialogManager.errorAuthDialog(this@FeedFragment)
            }

            override fun onEdit(post: Post) {
                if (userViewModel.isLogin()) {
                    viewModel.edit(post)
                } else DialogManager.errorAuthDialog(this@FeedFragment)
            }

            override fun onToVideo(post: Post) {
                post.attachment?.let {
                    findNavController().navigate(R.id.action_feedFragment_to_videoFragment,
                    Bundle().apply { url = it.url  })
                }
            }

            override fun onSwitchAudio(post: Post) {
                viewModel.switchAudio(post)
            }

            override fun onToPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply { id = post.id })
            }

            override fun onToUser(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_userWallFragment,
                    Bundle().apply { userId = post.authorId }
                )
            }
        })
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter { adapter.retry() },
            footer = PostLoadingStateAdapter { adapter.retry() }
        )

        subscribe(binding, adapter)
        setListeners(binding, adapter)

        return binding.root
    }

    private fun subscribe(binding: FragmentFeedBinding, adapter: PostsAdapter) {
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing =
                    it.refresh is LoadState.Loading
            }
        }

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id == 0L) return@observe
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.authState.collect {
                adapter.refresh()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.audioPlayerState.collect {
                viewModel.stopAudio()
            }
        }
    }

    private fun setListeners(binding: FragmentFeedBinding, adapter: PostsAdapter) {
        binding.add.setOnClickListener {
            if (userViewModel.isLogin()) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else DialogManager.errorAuthDialog(this)
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }
}