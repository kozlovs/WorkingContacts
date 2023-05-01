package ru.kozlovss.workingcontacts.data.eventsdata.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.userdata.dto.UserPreview

@Entity
data class EventEntity(
    @PrimaryKey
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    @Embedded
    val coords: Coordinates?,
    val eventType: Event.Type,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean,
    val speakerIds: List<Long> = emptyList(),
    val participantsIds: List<Long> = emptyList(),
    val participatedByMe: Boolean,
    @Embedded
    val attachment: Attachment?,
    val link: String?,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview> = emptyMap(),
    val isPaying: Boolean = false
) {
    fun toDto() = with(this) {
        Event(
            id,
            authorId,
            author,
            authorAvatar,
            authorJob,
            content,
            datetime,
            published,
            coords,
            eventType,
            likeOwnerIds,
            likedByMe,
            speakerIds,
            participantsIds,
            participatedByMe,
            attachment,
            link,
            ownedByMe,
            users,
            isPaying
        )
    }

    companion object {
        fun fromDto(event: Event) = with(event) {
            EventEntity(
                id,
                authorId,
                author,
                authorAvatar,
                authorJob,
                content,
                datetime,
                published,
                coords,
                type,
                likeOwnerIds,
                likedByMe,
                speakerIds,
                participantsIds,
                participatedByMe,
                attachment,
                link,
                ownedByMe,
                users,
                isPaying
            )
        }
    }
}

fun List<Event>.toEntity() = map(EventEntity.Companion::fromDto)