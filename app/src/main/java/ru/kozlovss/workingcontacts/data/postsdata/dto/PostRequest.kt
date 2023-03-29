package ru.kozlovss.workingcontacts.data.postsdata.dto

import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates

data class PostRequest(
    val id: Long,
    val content: String,
    val coords: Coordinates?,
    val link: String?,
    val attachment: Attachment?,
    val mentionIds: List<Long>?,
)