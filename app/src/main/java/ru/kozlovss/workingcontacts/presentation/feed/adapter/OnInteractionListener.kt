package ru.kozlovss.workingcontacts.presentation.feed.adapter

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onPlayVideo(post: Post)
    fun onToPost(post: Post)
    fun onToUser(post: Post)
}