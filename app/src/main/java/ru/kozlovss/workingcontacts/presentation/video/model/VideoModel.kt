package ru.kozlovss.workingcontacts.presentation.video.model

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

class VideoModel(
    val post: Post
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
        object RefreshingJobs: State
        object RefreshingPosts: State
    }
}