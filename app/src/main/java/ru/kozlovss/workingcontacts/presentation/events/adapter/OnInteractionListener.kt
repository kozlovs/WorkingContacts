package ru.kozlovss.workingcontacts.presentation.events.adapter

import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event

interface OnInteractionListener {
    fun onLike(event: Event)
    fun onParticipate(event: Event)
    fun onShare(event: Event)
    fun onRemove(event: Event)
    fun onEdit(event: Event)
    fun onToVideo(event: Event)
    fun onSwitchAudio(event: Event)
    fun onToEvent(event: Event)
    fun onToUser(event: Event)
}