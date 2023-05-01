package ru.kozlovss.workingcontacts.presentation.post.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.entity.Attachment
import ru.kozlovss.workingcontacts.entity.Post
import ru.kozlovss.workingcontacts.entity.User
import ru.kozlovss.workingcontacts.databinding.FragmentPostBinding
import ru.kozlovss.workingcontacts.presentation.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.util.Formatter
import ru.kozlovss.workingcontacts.presentation.util.LongArg
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.post.adapter.UsersPreviewAdapter
import ru.kozlovss.workingcontacts.presentation.feed.viewmodel.FeedViewModel
import ru.kozlovss.workingcontacts.presentation.map.ui.MapFragment.Companion.lat
import ru.kozlovss.workingcontacts.presentation.map.ui.MapFragment.Companion.lon
import ru.kozlovss.workingcontacts.presentation.newpost.ui.NewPostFragment.Companion.postId
import ru.kozlovss.workingcontacts.presentation.post.model.PostModel
import ru.kozlovss.workingcontacts.presentation.post.viewmodel.PostViewModel
import ru.kozlovss.workingcontacts.presentation.video.ui.VideoFragment.Companion.url

@AndroidEntryPoint
class PostFragment : Fragment() {
    private val feedViewModel: FeedViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels()
    private var binding: FragmentPostBinding? = null
    private var id: Long? = null
    private var adapter: UsersPreviewAdapter? = null

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
        initAdapter()
        subscribe()
        setListeners()
        postViewModel.updateData(id)

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        postViewModel.clearData()
        id = null
        binding = null
        adapter = null
    }

    private fun initAdapter() {
        adapter = UsersPreviewAdapter()
        binding!!.mentionsList.adapter = adapter
    }

    private fun subscribe() = with(binding!!) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.data.collect { post ->
                    post?.let { updateUi(it) }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.state.collectLatest { state ->
                    progress.isVisible = state is PostModel.State.Loading
                    cardLayout.isVisible = state is PostModel.State.Idle
                    errorLayout.isVisible = state is PostModel.State.Error
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.mentionsVisibility.collect {
                    mentionsCard.isVisible = it && !postViewModel.data.value?.mentionIds.isNullOrEmpty()
                    mentionsSelectorIcon.isChecked = it
                }
            }
        }
    }

    private fun updateUi(post: Post) = with(binding!!) {
        author.text = post.author
        authorJob.text = post.authorJob
        published.text = Formatter.localDateTimeToPostDateFormat(post.published)
        link.isVisible = post.link != null
        post.link?.let { link.text = post.link }
        content.text = post.content
        like.isChecked = post.likedByMe
        like.text = Formatter.numberToShortFormat(post.likeOwnerIds.size)
        menu.isVisible = post.ownedByMe
        mentionsCount.text = post.mentionIds.size.toString()
        mentionsSelector.isVisible = post.mentionIds.isNotEmpty()
        place.isVisible = post.coords != null
        adapter!!.submitList(post.mentionIds.map {
            val preview = post.users[it]!!
            User(
                it,
                preview.name,
                preview.name,
                preview.avatar
            )
        })


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
                    Glide.with(video)
                        .load(attachment.url)
                        .transform(RoundedCorners(30))
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(video)
                    image.visibility = View.GONE
                    audio.visibility = View.GONE
                }
            }
        } else {
            image.visibility = View.GONE
            videoLayout.visibility = View.GONE
            audio.visibility = View.GONE
        }
    }

    private fun setListeners() = with(binding!!) {

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
                                    findNavController().navigate(R.id.action_global_newPostFragment,
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
                findNavController().navigate(R.id.action_global_videoFragment,
                    Bundle().apply { url = it.url })
            }
        }

        switchButton.setOnClickListener {
            postViewModel.data.value?.let { post ->
                feedViewModel.switchAudio(post)
            }
        }

        mentionsSelector.setOnClickListener {
            postViewModel.switchMentionsVisibility()
        }

        place.setOnClickListener {
            postViewModel.data.value?.coords?.let { coords ->
                findNavController().navigate(R.id.action_global_mapFragment, Bundle().apply {
                    lat = coords.lat
                    lon = coords.longitude
                })
            }
        }
    }


    companion object {
        var Bundle.id: Long by LongArg
    }
}