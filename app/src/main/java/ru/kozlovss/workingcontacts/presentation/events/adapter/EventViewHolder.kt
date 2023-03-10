package ru.kozlovss.workingcontacts.presentation.events.adapter

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
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.databinding.CardEventBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {
        binding.apply {
            author.text = event.author
            authorJob.text = event.authorJob
            published.text = event.published
            if (event.link != null) {
                link.visibility = View.VISIBLE
                link.text = event.link
            } else {
                link.visibility = View.GONE
            }
            content.text = event.content
            like.isChecked = event.likedByMe
            like.text = Formatter.numberToShortFormat(event.likeOwnerIds.size)

            Glide.with(binding.avatar)
                .load(event.authorAvatar)
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(binding.avatar)

            val attachment = event.attachment
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

        setListeners(binding, event)
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

    private fun setListeners(binding: CardEventBinding, event: Event) = with(binding) {
        like.setOnClickListener {
            onInteractionListener.onLike(event)
        }

        share.setOnClickListener {
            onInteractionListener.onShare(event)
        }

        video.setOnClickListener {
            onInteractionListener.onPlayVideo(event)
        }

        cardEvent.setOnClickListener {
            onInteractionListener.onToEvent(event)
        }

        menu.isVisible = event.ownedByMe

        menu.setOnClickListener { view ->
            showMenu(view, event)
        }
    }

    private fun showMenu(v: View, event: Event) {
        PopupMenu(v.context, v).apply {
            inflate(R.menu.options_event_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove -> {
                        onInteractionListener.onRemove(event)
                        true
                    }
                    R.id.edit -> {
                        onInteractionListener.onEdit(event)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }
}