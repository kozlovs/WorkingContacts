package ru.kozlovss.workingcontacts.presentation.post.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.FragmentPostBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.domain.util.Formatter
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.feed.viewmodel.FeedViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.ui.NewPostFragment.Companion.postId
import ru.kozlovss.workingcontacts.presentation.post.model.PostModel
import ru.kozlovss.workingcontacts.presentation.post.viewmodel.PostViewModel
import ru.kozlovss.workingcontacts.presentation.video.VideoFragment.Companion.url

@AndroidEntryPoint
class PostFragment : Fragment() {
    private val feedViewModel: FeedViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var binding: FragmentPostBinding
    private var id: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(
            inflater,
            container,
            false
        )

        id = arguments?.id

        subscribe()
        setListeners()
        postViewModel.updateData(id)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        postViewModel.clearData()
    }

    private fun subscribe() {
        lifecycleScope.launchWhenCreated {
            postViewModel.data.collect { post ->
                post?.let { updateUi(it) }
            }
        }

        lifecycleScope.launchWhenCreated {
            postViewModel.state.collectLatest { state ->
                binding.progress.isVisible = state is PostModel.State.Loading
                binding.cardLayout.isVisible = state is PostModel.State.Idle
                binding.errorLayout.isVisible = state is PostModel.State.Error
            }
        }
    }

    private fun updateUi(post: Post) = with(binding) {
        author.text = post.author
        authorJob.text = post.authorJob
        published.text = Formatter.localDateTimeToPostDateFormat(post.published)
        if (post.link != null) {
            link.visibility = View.VISIBLE
            link.text = post.link
        } else {
            link.visibility = View.GONE
        }
        content.text = post.content
        like.isChecked = post.likedByMe
        like.text = Formatter.numberToShortFormat(post.likeOwnerIds.size)
        menu.isVisible = post.ownedByMe

        if (post.authorAvatar != null) {
            Glide.with(avatar)
                .load(post.authorAvatar)
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(avatar)
        } else {
            avatar.setImageResource(R.drawable.baseline_person_outline_24)
        }

        val attachment = post.attachment
        if (attachment != null) {
            when (attachment.type) {
                Attachment.Type.IMAGE -> {
                    image.visibility = View.VISIBLE
                    Glide.with(image)
                        .load(attachment.url)
                        .transform(RoundedCorners(30))
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(image)
                    videoLayout.visibility = View.GONE
                    audio.visibility = View.GONE
                }
                Attachment.Type.AUDIO -> {
                    audio.visibility = View.VISIBLE
                    image.visibility = View.GONE
                    videoLayout.visibility = View.GONE
                }
                Attachment.Type.VIDEO -> {
                    videoLayout.visibility = View.VISIBLE
                    val uri = Uri.parse(attachment.url)
                    video.setVideoURI(uri)
                    video.seekTo(1)
                    image.visibility = View.GONE
                    audio.visibility = View.GONE
                }
                else -> {
                    image.visibility = View.GONE
                    videoLayout.visibility = View.GONE
                    audio.visibility = View.GONE
                }
            }
        } else {
            image.visibility = View.GONE
            videoLayout.visibility = View.GONE
            audio.visibility = View.GONE
        }
    }

    private fun setListeners() = with(binding) {

        like.setOnClickListener {
            if (userViewModel.isLogin()) {
                postViewModel.likeById(id)
                ObjectAnimator.ofPropertyValuesHolder(
                    like,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            } else DialogManager.errorAuthDialog(this@PostFragment)
        }

        share.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postViewModel.data.value?.content)
                type = "text/plain"
            }
            val shareIntent =
                Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)
        }

        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options_post_menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            if (userViewModel.isLogin()) {
                                id?.let { id ->
                                    feedViewModel.removeById(id)
                                }
                            } else DialogManager.errorAuthDialog(this@PostFragment)
                            true
                        }
                        R.id.edit -> {
                            if (userViewModel.isLogin()) {
                                postViewModel.data.value?.let { post ->
                                    findNavController().navigate(R.id.action_postFragment_to_newPostFragment,
                                    Bundle().apply { postId = post.id })
                                }
                            } else DialogManager.errorAuthDialog(this@PostFragment)
                            true
                        }
                        else -> false
                    }

                }
            }.show()
        }

        video.setOnClickListener {
            postViewModel.data.value?.attachment?.let {
                findNavController().navigate(R.id.action_postFragment_to_videoFragment,
                    Bundle().apply { url = it.url })
            }
        }

        switchButton.setOnClickListener {
            postViewModel.data.value?.let { post ->
                feedViewModel.switchAudio(post)
            }
        }
    }


    companion object {
        var Bundle.id: Long by LongArg
    }
}