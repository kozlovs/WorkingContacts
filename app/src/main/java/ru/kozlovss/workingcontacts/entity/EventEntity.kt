package ru.kozlovss.workingcontacts.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kozlovss.workingcontacts.dto.Attachment
import ru.kozlovss.workingcontacts.dto.Coordinates
import ru.kozlovss.workingcontacts.dto.Event
import ru.kozlovss.workingcontacts.dto.UserPreview
import ru.kozlovss.workingcontacts.enumeration.EventType

@Entity
data class EventEntity(
    @PrimaryKey
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
    @Embedded
    val attachment: Attachment?,
    val link: String?,
    val ownedByMe: Boolean,
    val users: UserPreview//todo доработать после ответа куратора
) {
    fun toDto() = with(this) {
        Event(
            id,
            authorId,
            author,
            authorAvatar,
            authorJob,
            content,
            dateTime,
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

    companion object {
        fun fromDto(event: Event) = with(event) {
            EventEntity(
                id,
                authorId,
                author,
                authorAvatar,
                authorJob,
                content,
                dateTime,
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
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)