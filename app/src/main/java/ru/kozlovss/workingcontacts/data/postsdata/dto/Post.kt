package ru.kozlovss.workingcontacts.data.postsdata.dto

import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.dto.UserPreview

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
    val users: Map<Long, UserPreview>,
    val isPaying: Boolean = false
) {
    fun toRequest() = with(this) {
        PostRequest(
            id,
            content,
            coords,
            link,
            attachment,
            ownedByMe,
            mentionIds
        )
    }
}

