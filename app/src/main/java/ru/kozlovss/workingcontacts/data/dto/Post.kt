package ru.kozlovss.workingcontacts.data.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
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

data class PostRequest(
    val id: Long,
    val content: String,
    val coords: Coordinates?,
    val link: String?,
    val attachment: Attachment?,
    val ownedByMe: Boolean,
    val mentionIds: List<Long>?,
)

