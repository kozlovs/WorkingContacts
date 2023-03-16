package ru.kozlovss.workingcontacts.presentation.userswall.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.FragmentUserWallBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.presentation.feed.model.FeedModel
import ru.kozlovss.workingcontacts.presentation.userswall.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.userswall.adapter.PostsAdapter
import ru.kozlovss.workingcontacts.presentation.userswall.viewmodel.UserWallViewModel


@AndroidEntryPoint
class UserWallFragment : Fragment() {
    private val viewModel: UserWallViewModel by viewModels()
    private lateinit var binding: FragmentUserWallBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserWallBinding.inflate(inflater, container, false)

        viewModel.getUserData(arguments?.userId!!)
        viewModel.getPosts(arguments?.userId!!)
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (viewModel.isLogin()) viewModel.likeById(post.id)
                else DialogManager.errorAuthDialog(this@UserWallFragment)
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

            override fun onPlayVideo(post: Post) {
//                if (post.video.isNullOrBlank()) return
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
//                startActivity(intent)
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

        binding.list.adapter = adapter

        subscribe(binding, adapter)
        setListeners(binding)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearUserData()
        viewModel.cleanPosts()
    }

    private fun subscribe(binding: FragmentUserWallBinding, adapter: PostsAdapter) {

        lifecycleScope.launchWhenCreated {
            viewModel.userData.collect {
                it?.let {
                    binding.name.text = it.name
                    Glide.with(binding.avatar)
                        .load(it.avatar)
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(binding.avatar)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.postsData.collectLatest {
                Log.d("MyLog", "set data ${it.size}")
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

    private fun setListeners(binding: FragmentUserWallBinding) {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getPosts(arguments?.userId!!)
        }
    }

    companion object {
        var Bundle.userId: Long by LongArg
    }
}