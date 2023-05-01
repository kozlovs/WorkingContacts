package ru.kozlovss.workingcontacts.presentation.feed.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.CardPostBinding
import ru.kozlovss.workingcontacts.presentation.util.Formatter

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) = with(binding) {
        author.text = post.author
        authorJob.text = post.authorJob
        published.text = Formatter.localDateTimeToPostDateFormat(post.published)
        link.isVisible = post.link != null
        post.link?.let { link.text = post.link }
        content.text = post.content
        like.isChecked = post.likedByMe
        like.text = Formatter.numberToShortFormat(post.likeOwnerIds.size)
        audioButton.isChecked = post.isPaying == true
        menu.isVisible = post.ownedByMe
        mentionsCount.text = post.mentionIds.size.toString()
        mentionsCount.isVisible = post.mentionIds.isNotEmpty()
        mentionsIcon.isVisible = post.mentionIds.isNotEmpty()
        placeIcon.isVisible = post.coords != null

        if (post.authorAvatar != null) {
            Glide.with(binding.avatar)
                .load(post.authorAvatar)
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(binding.avatar)
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
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(image)
                    video.visibility = View.GONE
                    videoIcon.visibility = View.GONE
                    audioButton.visibility = View.GONE
                    audioName.visibility = View.GONE
                }

                Attachment.Type.AUDIO -> {
                    audioButton.visibility = View.VISIBLE
                    audioName.visibility = View.VISIBLE
                    image.visibility = View.GONE
                    videoIcon.visibility = View.GONE
                    video.visibility = View.GONE
                }

                Attachment.Type.VIDEO -> {
                    video.visibility = View.VISIBLE
                    videoIcon.visibility = View.VISIBLE
                    Glide.with(video)
                        .load(attachment.url)
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(video)
                    image.visibility = View.GONE
                    audioButton.visibility = View.GONE
                    audioName.visibility = View.GONE
                }
            }
        } else {
            image.visibility = View.GONE
            video.visibility = View.GONE
            videoIcon.visibility = View.GONE
            audioButton.visibility = View.GONE
            audioName.visibility = View.GONE
        }

        setListeners(post)
    }

    fun bind(payload: Payload) = with(binding) {
        payload.likedByMe?.let {
            like.isChecked = it
            ObjectAnimator.ofPropertyValuesHolder(
                like,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
            ).start()
        }
        payload.likes?.let {
            like.text = Formatter.numberToShortFormat(it)
        }
        payload.content?.let {
            content.text = it
        }
        payload.isPlay?.let {
            audioButton.isChecked = it
        }
    }

    private fun setListeners(post: Post) = with(binding) {
        like.setOnClickListener {
            onInteractionListener.onLike(post)
        }

        share.setOnClickListener {
            onInteractionListener.onShare(post)
        }

        video.setOnClickListener {
            onInteractionListener.onToVideo(post)
        }

        audioButton.setOnClickListener {
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