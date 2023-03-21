package ru.kozlovss.workingcontacts.presentation.userswall.adapter

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onToVideo(post: Post)
    fun onSwitchAudio(post: Post)
    fun onToPost(post: Post)
}