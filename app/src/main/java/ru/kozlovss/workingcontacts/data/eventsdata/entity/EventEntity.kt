package ru.kozlovss.workingcontacts.data.eventsdata.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.dto.UserPreview

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
    val users: Map<Long, UserPreview> = emptyMap()
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
            users
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
                users
            )
        }
    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity.Companion::fromDto)