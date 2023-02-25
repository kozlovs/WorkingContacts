package ru.kozlovss.workingcontacts.data.eventsdata.dto

import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates

data class EventRequest(
    val id: Long,
    val content: String,
    val dateTime: String,
    val published: String,
    val coords: Coordinates?,
    val type: Event.EventType?,
    val attachment: Attachment?,
    val link: String?,
    val speakerIds: List<Long>?,
)