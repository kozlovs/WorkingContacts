package ru.kozlovss.workingcontacts.data.eventsdata.dto

import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.dto.UserPreview
data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates?,
    val type: Type,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean,
    val speakerIds: List<Long> = emptyList(),
    val participantsIds: List<Long> = emptyList(),
    val participatedByMe: Boolean,
    val attachment: Attachment?,
    val link: String?,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview>,
    val isPaying: Boolean = false
) {
    fun toRequest() = with(this) {
        EventRequest(
            id,
            content,
            datetime,
            published,
            coords,
            type,
            attachment,
            link,
            speakerIds
        )
    }

    enum class Type {
        OFFLINE, ONLINE
    }
}