package ru.kozlovss.workingcontacts.presentation.mywall.adapter.posts

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
import ru.kozlovss.workingcontacts.databinding.CardPostBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) = with(binding) {
        author.visibility = View.GONE
        authorJob.visibility = View.GONE
        avatar.visibility = View.GONE
        barrierInfo.visibility = View.GONE
        menu.visibility = View.GONE
        published.text = Formatter.localDateTimeToPostDateFormat(post.published)
        content.text = post.content
        link.isVisible = post.link != null
        post.link?.let { link.text = post.link }
        like.isChecked = post.likedByMe
        like.text = Formatter.numberToShortFormat(post.likeOwnerIds.size)
        audioButton.isChecked = post.isPaying == true
        mentionsCount.text = post.mentionIds.size.toString()
        mentionsCount.isVisible = post.mentionIds.isNotEmpty()
        mentionsIcon.isVisible = post.mentionIds.isNotEmpty()
        placeIcon.isVisible = post.coords != null

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

        cardPost.setOnLongClickListener { view ->
            showMenu(view, post)
            true
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