package ru.kozlovss.workingcontacts.presentation.userswall.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.FragmentPostsListBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.userswall.adapter.posts.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.userswall.adapter.posts.PostsAdapter
import ru.kozlovss.workingcontacts.presentation.userswall.model.UserWallModel
import ru.kozlovss.workingcontacts.presentation.userswall.viewmodel.UserWallViewModel
import ru.kozlovss.workingcontacts.presentation.video.VideoFragment.Companion.url

@AndroidEntryPoint
class PostsListFragment : Fragment() {

    private lateinit var binding: FragmentPostsListBinding
    private val viewModel: UserWallViewModel by activityViewModels()
    private lateinit var adapter: PostsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        subscribe()
        setListeners()
    }

    private fun subscribe() = with(binding) {
        lifecycleScope.launchWhenStarted {
            viewModel.postsData.collect {
                adapter.submitList(it)
                empty.isVisible = it.isEmpty()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.state.collectLatest { state ->
                swipeRefresh.isRefreshing = state is UserWallModel.State.RefreshingPosts
            }
        }
    }

    private fun init() = with(binding) {
        list.layoutManager = LinearLayoutManager(activity)
        adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (viewModel.isLogin()) viewModel.likeById(post.id)
                else DialogManager.errorAuthDialog(this@PostsListFragment)
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

            override fun onToVideo(post: Post) {
                post.attachment?.let {
                    findNavController().navigate(
                        R.id.action_userWallFragment_to_videoFragment,
                        Bundle().apply { url = it.url })
                }
            }

            override fun onSwitchAudio(post: Post) {
                viewModel.switchAudio(post)
            }

            override fun onToPost(post: Post) {
//                findNavController().navigate(
//                    R.id.action_feedFragment_to_postFragment,
//                    Bundle().apply { id = post.id })
            }
        })
        list.adapter = adapter
    }

    private fun setListeners() = with(binding) {
        swipeRefresh.setOnRefreshListener {
            viewModel.userData.value?.let {
                viewModel.getPosts()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PostsListFragment()
    }
}