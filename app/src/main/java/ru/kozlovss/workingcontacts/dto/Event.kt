package ru.kozlovss.workingcontacts.dto

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
    val type: String,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean,
    val speakerIds: List<Long> = emptyList(),
    val participantsIds: List<Long> = emptyList(),
    val participatedByMe: Boolean,
    val attachment: Attachment?,
    val link: String?,
    val ownedByMe: Boolean,
    val users: UserPreview//todo доработать после ответа куратора
)

enum class EventType {
    OFFLINE, ONLINE
}
