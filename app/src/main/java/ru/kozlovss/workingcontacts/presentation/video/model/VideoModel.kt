package ru.kozlovss.workingcontacts.presentation.video.model

import ru.kozlovss.workingcontacts.entity.Post

class VideoModel(
    val post: Post
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}