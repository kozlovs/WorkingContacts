package ru.kozlovss.workingcontacts.presentation.userswall.adapter.posts

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.CardWallPostBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter

class PostViewHolder(
    private val binding: CardWallPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) = with(binding) {
        published.text = Formatter.localDateTimeToPostDateFormat(post.published)
        content.text = post.content
        if (post.link != null) {
            link.visibility = View.VISIBLE
            link.text = post.link
        } else {
            link.visibility = View.GONE
        }
        like.isChecked = post.likedByMe
        like.text = Formatter.numberToShortFormat(post.likeOwnerIds.size)
        switchButton.isChecked = post.isPaying == true
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
                    Glide.with(videoPreview)
                        .load(attachment.url)
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(videoPreview)
                    image.visibility = View.GONE
                    audio.visibility = View.GONE
                }
            }
        } else {
            image.visibility = View.GONE
            videoLayout.visibility = View.GONE
            audio.visibility = View.GONE
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
            switchButton.isChecked = it
        }
    }

    private fun setListeners(post: Post) = with(binding) {
        like.setOnClickListener {
            onInteractionListener.onLike(post)
        }

        share.setOnClickListener {
            onInteractionListener.onShare(post)
        }

        videoPreview.setOnClickListener {
            onInteractionListener.onToVideo(post)
        }

        switchButton.setOnClickListener {
            onInteractionListener.onSwitchAudio(post)
        }

        cardPost.setOnClickListener {
            onInteractionListener.onToPost(post)
        }
    }
}