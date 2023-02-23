package ru.kozlovss.workingcontacts.data.dto

import ru.kozlovss.workingcontacts.data.enumeration.EventType

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val dateTime: String,
    val published: String,
    val coords: Coordinates?,
    val type: EventType,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean,
    val speakerIds: List<Long> = emptyList(),
    val participantsIds: List<Long> = emptyList(),
    val participatedByMe: Boolean,
    val attachment: Attachment?,
    val link: String?,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview>
)

data class EventRequest(
    val id: Long,
    val content: String,
    val dateTime: String,
    val published: String,
    val coords: Coordinates?,
    val type: EventType?,
    val attachment: Attachment?,
    val link: String?,
    val speakerIds: List<Long>?,
)