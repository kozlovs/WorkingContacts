package ru.kozlovss.workingcontacts.presentation.feed.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepositoryImpl
import ru.kozlovss.workingcontacts.databinding.CardPostBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = Formatter.numberToShortFormat(post.likeOwnerIds.size)

            Glide.with(binding.avatar)
                .load(post.authorAvatar)
                .transform(RoundedCorners(30))
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(binding.avatar)

            val attachment = post.attachment
            if (attachment != null) {
                if (attachment.attachmentType == Attachment.AttachmentType.IMAGE) {
                    image.visibility = View.VISIBLE
                    Glide.with(image)
                        .load(PostRepositoryImpl.getImageUrl(attachment.url))
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(image)
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

    private fun setListeners(binding: CardPostBinding, post: Post) = with(binding) {
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

        menu.isVisible = post.ownedByMe

        menu.setOnClickListener {
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
        }

        image.setOnClickListener {
            onInteractionListener.onToImage(post)
        }
    }
}