package ru.kozlovss.workingcontacts.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.dto.Post
import ru.kozlovss.workingcontacts.data.dto.UserPreview

@Entity
data class PostEntity(
    @PrimaryKey
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String?,
    val content: String,
    val published: String,
    @Embedded
    val coords: Coordinates? = null,
    val link: String? = null,
    val likeOwnerIds: List<Long> = emptyList(),
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded
    val attachment: Attachment? = null,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview> = emptyMap()
) {
    fun toDto() = with(this) {
        Post(
            id,
            authorId,
            author,
            authorAvatar,
            authorJob,
            content,
            published,
            coords,
            link,
            likeOwnerIds,
            mentionIds,
            mentionedMe,
            likedByMe,
            attachment,
            ownedByMe,
            users
        )
    }

    companion object {
        fun fromDto(post: Post) = with(post) {
            PostEntity(
                id,
                authorId,
                author,
                authorAvatar,
                authorJob,
                content,
                published,
                coords,
                link,
                likeOwnerIds,
                mentionIds,
                mentionedMe,
                likedByMe,
                attachment,
                ownedByMe,
                users
            )
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity.Companion::fromDto)