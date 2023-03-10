package ru.kozlovss.workingcontacts.presentation.mywall.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.CardWallPostBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter

class PostViewHolder (
    private val binding: CardWallPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = Formatter.numberToShortFormat(post.likeOwnerIds.size)

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
                        video.visibility = View.GONE
                        audio.visibility = View.GONE
                    }
                    Attachment.Type.AUDIO -> {
                        audio.visibility = View.VISIBLE
                        image.visibility = View.GONE
                        video.visibility = View.GONE
                    }
                    Attachment.Type.VIDEO -> {
                        video.visibility = View.VISIBLE
                        image.visibility = View.GONE
                        audio.visibility = View.GONE
                    }
                    else -> {
                        image.visibility = View.GONE
                        video.visibility = View.GONE
                        audio.visibility = View.GONE
                    }
                }
            } else {
                image.visibility = View.GONE
                video.visibility = View.GONE
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
                ObjectAnimator.ofFloat(
                    binding.like,
                    View.ROTATION,
                    0F, 360F
                ).start()
            }
        }
        payload.likes?.let {
            binding.like.text = Formatter.numberToShortFormat(it)
        }
        payload.content?.let {
            binding.content.text = it
        }
    }

    private fun setListeners(binding: CardWallPostBinding, post: Post) = with(binding) {
        like.setOnClickListener {
            onInteractionListener.onLike(post)
        }

        share.setOnClickListener {
            onInteractionListener.onShare(post)
        }

        video.setOnClickListener {
            onInteractionListener.onPlayVideo(post)
        }

        cardPost.setOnClickListener {
            onInteractionListener.onToPost(post)
        }

        cardPost.setOnLongClickListener {
            PopupMenu(it.context, it).apply {
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
            true
        }
    }
}