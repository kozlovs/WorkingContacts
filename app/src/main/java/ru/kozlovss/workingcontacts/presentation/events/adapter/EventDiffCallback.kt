package ru.kozlovss.workingcontacts.presentation.events.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem

    override fun getChangePayload(oldItem: Event, newItem: Event): Any =
        Payload(
            likedByMe = newItem.likedByMe.takeIf { it != oldItem.likedByMe },
            likes = newItem.likeOwnerIds.size.takeIf { it != oldItem.likeOwnerIds.size },
            content = newItem.content.takeIf { it != oldItem.content }
        )
}