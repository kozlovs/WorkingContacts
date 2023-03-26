package ru.kozlovss.workingcontacts.presentation.mywall.adapter.posts

import androidx.recyclerview.widget.DiffUtil
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem

    override fun getChangePayload(oldItem: Post, newItem: Post): Any =
        Payload(
            likedByMe = newItem.likedByMe.takeIf { it != oldItem.likedByMe },
            likes = newItem.likeOwnerIds.size.takeIf { it != oldItem.likeOwnerIds.size },
            content = newItem.content.takeIf { it != oldItem.content },
            isPlay = newItem.isPaying.takeIf { it != oldItem.isPaying }
        )
}