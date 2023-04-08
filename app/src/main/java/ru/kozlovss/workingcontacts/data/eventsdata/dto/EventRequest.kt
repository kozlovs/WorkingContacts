package ru.kozlovss.workingcontacts.data.eventsdata.dto

import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates

data class EventRequest(
    val id: Long,
    val content: String,
    val datetime: String?,
    val coords: Coordinates?,
    val type: Event.Type?,
    val attachment: Attachment?,
    val link: String?,
    val speakerIds: List<Long>,
)