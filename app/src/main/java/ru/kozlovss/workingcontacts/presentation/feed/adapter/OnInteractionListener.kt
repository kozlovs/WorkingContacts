package ru.kozlovss.workingcontacts.presentation.feed.adapter

import ru.kozlovss.workingcontacts.entity.Post

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onToVideo(post: Post)
    fun onSwitchAudio(post: Post)
    fun onToPost(post: Post)
    fun onToUser(post: Post)
}