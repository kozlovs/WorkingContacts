package ru.kozlovss.workingcontacts.presentation.mywall.adapter.posts

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onToVideo(post: Post)
    fun onSwitchAudio(post: Post)
    fun onToPost(post: Post)
}