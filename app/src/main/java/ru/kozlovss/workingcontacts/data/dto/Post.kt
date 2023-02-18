package ru.kozlovss.workingcontacts.data.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val authorAvatar: String?,
    val authorJob: String?,
    val published: String,
    val coords: Coordinates?,
    val link: String?,
    val likeOwnerIds: List<Long> = emptyList(),
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    val attachment: Attachment?,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview>
)
