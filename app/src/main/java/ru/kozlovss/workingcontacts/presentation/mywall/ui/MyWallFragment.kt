package ru.kozlovss.workingcontacts.presentation.mywall.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.FragmentMyWallBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.PostLoadingStateAdapter
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.PostsAdapter
import ru.kozlovss.workingcontacts.presentation.mywall.viewmodel.MyWallViewModel

@AndroidEntryPoint
class MyWallFragment : Fragment() {

    private val viewModel: MyWallViewModel by viewModels()
    private lateinit var binding: FragmentMyWallBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyWallBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (viewModel.isLogin()) viewModel.likeById(post.id)
                else DialogManager.errorAuthDialog(this@MyWallFragment)
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
                viewModel.edit(post)
            }

            override fun onPlayVideo(post: Post) {
//                if (post.video.isNullOrBlank()) return
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
//                startActivity(intent)
            }

            override fun onToPost(post: Post) {
//                findNavController().navigate(
//                    R.id.action_feedFragment_to_postFragment,
//                    Bundle().apply { id = post.id })
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

    private fun subscribe(binding: FragmentMyWallBinding, adapter: PostsAdapter) {
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
            findNavController().navigate(R.id.action_myWallFragment_to_newPostFragment)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.authState.collect {
                adapter.refresh()
            }
        }
    }

    private fun setListeners(binding: FragmentMyWallBinding, adapter: PostsAdapter) {
        binding.add.setOnClickListener {
            if (viewModel.isLogin()) findNavController().navigate(R.id.action_myWallFragment_to_newPostFragment)
            else DialogManager.errorAuthDialog(this@MyWallFragment)
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }
}