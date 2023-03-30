package ru.kozlovss.workingcontacts.presentation.mywall.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.FragmentMyPostsListBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.posts.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.posts.PostLoadingStateAdapter
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.posts.PostsAdapter
import ru.kozlovss.workingcontacts.presentation.mywall.model.MyWallModel
import ru.kozlovss.workingcontacts.presentation.mywall.viewmodel.MyWallViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.ui.NewPostFragment.Companion.postId
import ru.kozlovss.workingcontacts.presentation.post.ui.PostFragment.Companion.id
import ru.kozlovss.workingcontacts.presentation.video.VideoFragment.Companion.url

@AndroidEntryPoint
class MyPostsListFragment : Fragment() {

    private lateinit var binding: FragmentMyPostsListBinding
    private val viewModel: MyWallViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPostsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        subscribe()
        setListeners()
    }

    private fun subscribe() = with(binding) {
        lifecycleScope.launchWhenCreated {
            viewModel.postData.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
                empty.isVisible = adapter.itemCount < 1 && viewModel.state.value is MyWallModel.State.Idle
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.state.collectLatest { state ->
                errorLayout.isVisible = state is MyWallModel.State.Error
                empty.isVisible = adapter.itemCount < 1 && viewModel.state.value is MyWallModel.State.Idle
            }
        }

        lifecycleScope.launchWhenCreated {
            userViewModel.token.collect { token ->
                token?.let {
                    adapter.refresh()
                } ?: viewModel.clearMyData()
            }
        }
    }

    private fun init() = with(binding) {
        list.layoutManager = LinearLayoutManager(activity)
        adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (viewModel.isLogin()) viewModel.likeById(post.id)
                else DialogManager.errorAuthDialog(this@MyPostsListFragment)
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
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                if (userViewModel.isLogin()) {
                    findNavController().navigate(R.id.action_myWallFragment_to_newPostFragment,
                        Bundle().apply { postId = post.id })
                } else DialogManager.errorAuthDialog(this@MyPostsListFragment)
            }

            override fun onToVideo(post: Post) {
                post.attachment?.let {
                    findNavController().navigate(
                        R.id.action_myWallFragment_to_videoFragment,
                        Bundle().apply { url = it.url })
                }
            }

            override fun onSwitchAudio(post: Post) {
                viewModel.switchAudio(post)
            }

            override fun onToPost(post: Post) {
                findNavController().navigate(
                    R.id.action_myWallFragment_to_postFragment,
                    Bundle().apply { id = post.id })
            }
        })

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter { adapter.retry() },
            footer = PostLoadingStateAdapter { adapter.retry() }
        )
    }

    private fun setListeners() = with(binding) {
        swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyPostsListFragment()
    }
}