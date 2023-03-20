package ru.kozlovss.workingcontacts.presentation.feed.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.net.Uri
import android.view.View
import android.widget.MediaController
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.CardPostBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter
import ru.kozlovss.workingcontacts.presentation.video.VideoFragment.Companion.url

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            authorJob.text = post.authorJob
            published.text = post.published
            if (post.link != null) {
                link.visibility = View.VISIBLE
                link.text = post.link
            } else {
                link.visibility = View.GONE
            }
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = Formatter.numberToShortFormat(post.likeOwnerIds.size)
            switchButton.isChecked = post.isPaying == true
            menu.isVisible = post.ownedByMe

            if (post.authorAvatar != null) {
                Glide.with(binding.avatar)
                    .load(post.authorAvatar)
                    .placeholder(R.drawable.baseline_update_24)
                    .error(R.drawable.baseline_error_outline_24)
                    .timeout(10_000)
                    .into(binding.avatar)
            }

            val attachment = post.attachment
            if (attachment != null) {
                when (attachment.type) {
                    Attachment.Type.IMAGE -> {
                        image.visibility = View.VISIBLE
                        Glide.with(image)
                            .load(attachment.url)
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

        setListeners(binding, post)
    }

    fun bind(payload: Payload) {
        payload.likedByMe?.let {
            binding.like.isChecked = it
            if (it) {
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.like,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            } else {
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.like,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            }
        }
        payload.likes?.let {
            binding.like.text = Formatter.numberToShortFormat(it)
        }
        payload.content?.let {
            binding.content.text = it
        }
        payload.isPlay?.let {
            binding.switchButton.isChecked = it
        }
    }

    private fun setListeners(binding: CardPostBinding, post: Post) = with(binding) {
        like.setOnClickListener {
            onInteractionListener.onLike(post)
        }

        share.setOnClickListener {
            onInteractionListener.onShare(post)
        }

        video.setOnClickListener {
            onInteractionListener.onToVideo(post)
        }

        switchButton.setOnClickListener {
            onInteractionListener.onSwitchAudio(post)
        }

        cardPost.setOnClickListener {
            onInteractionListener.onToPost(post)
        }

        avatar.setOnClickListener {
            onInteractionListener.onToUser(post)
        }

        author.setOnClickListener {
            onInteractionListener.onToUser(post)
        }

        authorJob.setOnClickListener {
            onInteractionListener.onToUser(post)
        }

        menu.setOnClickListener { view ->
            showMenu(view, post)
        }
    }

    private fun showMenu(v: View, post: Post) {
        PopupMenu(v.context, v).apply {
            inflate(R.menu.options_post_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove -> {
                        onInteractionListener.onRemove(post)
                        true
                    }
                    R.id.edit -> {
                        onInteractionListener.onEdit(post)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }
}