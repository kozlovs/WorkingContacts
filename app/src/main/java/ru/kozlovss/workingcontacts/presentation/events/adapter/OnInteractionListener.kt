package ru.kozlovss.workingcontacts.presentation.events.adapter

import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event

interface OnInteractionListener {
    fun onLike(event: Event)
    fun onShare(event: Event)
    fun onRemove(event: Event)
    fun onEdit(event: Event)
    fun onPlayVideo(event: Event)
    fun onToEvent(event: Event)
}