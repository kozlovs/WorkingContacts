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
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.FragmentMyWallBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.PostLoadingStateAdapter
import ru.kozlovss.workingcontacts.presentation.mywall.adapter.PostsAdapter
import ru.kozlovss.workingcontacts.presentation.mywall.viewmodel.MyWallViewModel
import ru.kozlovss.workingcontacts.presentation.feed.ui.PostFragment.Companion.id
import ru.kozlovss.workingcontacts.presentation.mywall.model.MyWallModel
import ru.kozlovss.workingcontacts.presentation.video.VideoFragment.Companion.url

@AndroidEntryPoint
class MyWallFragment : Fragment() {

    private val myWallViewModel: MyWallViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentMyWallBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyWallBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (myWallViewModel.isLogin()) myWallViewModel.likeById(post.id)
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
                myWallViewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                myWallViewModel.edit(post)
            }

            override fun onToVideo(post: Post) {
                post.attachment?.let {
                    findNavController().navigate(R.id.action_myWallFragment_to_videoFragment,
                        Bundle().apply { url = it.url  })
                }
            }

            override fun onSwitchAudio(post: Post) {
                myWallViewModel.switchAudio(post)
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
        subscribe(binding, adapter)
        setListeners(binding, adapter)

        return binding.root
    }

    private fun subscribe(binding: FragmentMyWallBinding, adapter: PostsAdapter) {
        lifecycleScope.launchWhenStarted {
            myWallViewModel.myData.collect {
                it?.let {
                    binding.name.text = it.name
                    if (it.avatar != null) {
                        Glide.with(binding.avatar)
                            .load(it.avatar)
                            .placeholder(R.drawable.baseline_update_24)
                            .error(R.drawable.baseline_error_outline_24)
                            .timeout(10_000)
                            .into(binding.avatar)
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            myWallViewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing =
                    it.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            myWallViewModel.state.collectLatest { state ->
                binding.authButtons.isVisible = state is MyWallModel.MyWallModelState.NoLogin
                binding.progress.isVisible = state is MyWallModel.MyWallModelState.Loading
                binding.myCard.isVisible = state is MyWallModel.MyWallModelState.Idle
                binding.swipeRefresh.isVisible = state is MyWallModel.MyWallModelState.Idle
                binding.add.isVisible = state is MyWallModel.MyWallModelState.Idle
            }
        }

        myWallViewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id == 0L) return@observe
            findNavController().navigate(R.id.action_myWallFragment_to_newPostFragment)
        }

        lifecycleScope.launchWhenCreated {
            userViewModel.token.collect { token ->
                myWallViewModel.updateMyData(token)
                if (token != null) {
                    adapter.refresh()
                } else {
                    myWallViewModel.clearMyData()
                }
            }
        }
    }

    private fun setListeners(binding: FragmentMyWallBinding, adapter: PostsAdapter) = with(binding) {
        add.setOnClickListener {
            if (myWallViewModel.isLogin()) findNavController().navigate(R.id.action_myWallFragment_to_newPostFragment)
            else DialogManager.errorAuthDialog(this@MyWallFragment)
        }

        buttonLogout.setOnClickListener {
            userViewModel.logout()
        }

        swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }

        onLogInButton.setOnClickListener {
            findNavController().navigate(R.id.action_myWallFragment_to_authorizationFragment)
        }

        onSignInButton.setOnClickListener {
            findNavController().navigate(R.id.action_myWallFragment_to_registrationFragment)
        }
    }
}