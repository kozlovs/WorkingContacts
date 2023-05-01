package ru.kozlovss.workingcontacts.entity

data class PostRequest(
    val id: Long,
    val content: String,
    val coords: Coordinates?,
    val link: String?,
    val attachment: Attachment?,
    val mentionIds: List<Long>,
)