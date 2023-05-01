package ru.kozlovss.workingcontacts.presentation.userswall.adapter.posts

import ru.kozlovss.workingcontacts.entity.Post

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onToVideo(post: Post)
    fun onSwitchAudio(post: Post)
    fun onToPost(post: Post)
}