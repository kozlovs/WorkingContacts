package ru.kozlovss.workingcontacts.presentation.feed.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.entity.Post
import ru.kozlovss.workingcontacts.databinding.FragmentFeedBinding
import ru.kozlovss.workingcontacts.domain.error.ErrorEvent
import ru.kozlovss.workingcontacts.presentation.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.activity.MainActivity
import ru.kozlovss.workingcontacts.presentation.feed.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.feed.adapter.PostLoadingStateAdapter
import ru.kozlovss.workingcontacts.presentation.feed.adapter.PostsAdapter
import ru.kozlovss.workingcontacts.presentation.feed.viewmodel.FeedViewModel
import ru.kozlovss.workingcontacts.presentation.post.ui.PostFragment.Companion.id
import ru.kozlovss.workingcontacts.presentation.userswall.ui.UserWallFragment.Companion.userId
import ru.kozlovss.workingcontacts.presentation.video.ui.VideoFragment.Companion.url
import ru.kozlovss.workingcontacts.presentation.newpost.ui.NewPostFragment.Companion.postId

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: FeedViewModel by activityViewModels()
    private var adapter: PostsAdapter? = null
    private var binding: FragmentFeedBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
        resetNvaViewState()
        initAdapter()
        subscribe()
        setListeners()

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding = null
    }

    private fun resetNvaViewState() {
        val activity = activity as MainActivity
        activity.resetNvaViewState()
    }

    private fun initAdapter() = with(binding!!) {
        adapter = PostsAdapter(
            object : OnInteractionListener {
                override fun onLike(post: Post) {
                    if (viewModel.isLogin()) {
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
                    if (viewModel.isLogin()) {
                        viewModel.removeById(post.id)
                    } else DialogManager.errorAuthDialog(this@FeedFragment)
                }

                override fun onEdit(post: Post) {
                    if (viewModel.isLogin()) {
                        findNavController().navigate(R.id.action_global_newPostFragment,
                            Bundle().apply { postId = post.id })
                    } else DialogManager.errorAuthDialog(this@FeedFragment)
                }

                override fun onToVideo(post: Post) {
                    post.attachment?.let {
                        findNavController().navigate(R.id.action_global_videoFragment,
                            Bundle().apply { url = it.url })
                    }
                }

                override fun onSwitchAudio(post: Post) {
                    viewModel.switchAudio(post)
                }

                override fun onToPost(post: Post) {
                    findNavController().navigate(
                        R.id.action_global_postFragment,
                        Bundle().apply { id = post.id })
                }

                override fun onToUser(post: Post) {
                    findNavController().navigate(
                        R.id.action_global_userWallFragment,
                        Bundle().apply { userId = post.authorId }
                    )
                }
            },
            requireContext()
        )
        list.adapter = adapter!!.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter { adapter!!.retry() },
            footer = PostLoadingStateAdapter { adapter!!.retry() }
        )
    }

    private fun subscribe() = with(binding!!) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter!!::submitData)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter!!.loadStateFlow.collectLatest {
                    swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect {
                    when (it) {
                        is ErrorEvent.ApiErrorMassage -> showSnackBar(root, "Error: ${it.message}")
                        ErrorEvent.AuthErrorMassage -> showSnackBar(root, "Error authentication")
                        ErrorEvent.NetworkErrorMassage -> showSnackBar(root, "Error network")
                        ErrorEvent.UnknownErrorMassage -> showSnackBar(root, "Unknown error")
                    }
                }
            }
        }
    }

    private fun showSnackBar(view: View, massage: String) {
        Snackbar
            .make(view, massage, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun setListeners() = with(binding!!) {
        add.setOnClickListener {
            if (viewModel.isLogin()) {
                findNavController().navigate(R.id.action_global_newPostFragment)
            } else DialogManager.errorAuthDialog(this@FeedFragment)
        }

        swipeRefresh.setOnRefreshListener(adapter!!::refresh)
    }
}