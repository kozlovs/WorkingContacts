package ru.kozlovss.workingcontacts.presentation.events.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.databinding.CardEventBinding
import ru.kozlovss.workingcontacts.presentation.util.Formatter

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {
        binding.apply {
            author.text = event.author
            authorJob.text = event.authorJob
            published.text = Formatter.localDateTimeToPostDateFormat(event.published)
            link.isVisible = event.link != null
            event.link?.let { link.text = event.link }
            content.text = event.content
            like.isChecked = event.likedByMe
            like.text = Formatter.numberToShortFormat(event.likeOwnerIds.size)
            participate.isChecked = event.participatedByMe
            participate.text = Formatter.numberToShortFormat(event.participantsIds.size)
            menu.isVisible = event.ownedByMe
            speakersCount.text = event.speakerIds.size.toString()
            placeIcon.isVisible = event.coords != null
            if (event.type == Event.Type.ONLINE) typeIcon.setImageResource(R.drawable.baseline_online_24)
            else typeIcon.setImageResource(R.drawable.baseline_people_24)
            dateTime.text = Formatter.localDateTimeToPostDateFormat(event.datetime)

            if (event.authorAvatar != null) {
                Glide.with(binding.avatar)
                    .load(event.authorAvatar)
                    .placeholder(R.drawable.baseline_update_24)
                    .error(R.drawable.baseline_error_outline_24)
                    .timeout(10_000)
                    .into(binding.avatar)
            } else {
                avatar.setImageResource(R.drawable.baseline_person_outline_24)
            }

            val attachment = event.attachment
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
        }

        setListeners(event)
    }

    fun bind(payload: Payload) = with(binding) {
        payload.likedByMe?.let {
            like.isChecked = it
            if (it) {
                ObjectAnimator.ofPropertyValuesHolder(
                    like,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            } else {
                ObjectAnimator.ofPropertyValuesHolder(
                    like,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            }
        }
        payload.likes?.let {
            like.text = Formatter.numberToShortFormat(it)
        }
        payload.participatedByMe?.let {
            participate.isChecked = it
            if (it) {
                ObjectAnimator.ofPropertyValuesHolder(
                    participate,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            } else {
                ObjectAnimator.ofPropertyValuesHolder(
                    participate,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            }
        }
        payload.participateIds?.let {
            participate.text = Formatter.numberToShortFormat(it)
        }
        payload.content?.let {
            content.text = it
        }
        payload.isPlay?.let {
            audioButton.isChecked = it
        }
    }

    private fun setListeners(event: Event) = with(binding) {
        like.setOnClickListener {
            onInteractionListener.onLike(event)
        }

        participate.setOnClickListener {
            onInteractionListener.onParticipate(event)
        }

        share.setOnClickListener {
            onInteractionListener.onShare(event)
        }

        video.setOnClickListener {
            onInteractionListener.onToVideo(event)
        }

        audioButton.setOnClickListener {
            onInteractionListener.onSwitchAudio(event)
        }

        cardEvent.setOnClickListener {
            onInteractionListener.onToEvent(event)
        }

        menu.setOnClickListener { view ->
            showMenu(view, event)
        }

        avatar.setOnClickListener {
            onInteractionListener.onToUser(event)
        }

        author.setOnClickListener {
            onInteractionListener.onToUser(event)
        }

        authorJob.setOnClickListener {
            onInteractionListener.onToUser(event)
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