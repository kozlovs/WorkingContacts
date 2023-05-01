package ru.kozlovss.workingcontacts.presentation.post.model

import ru.kozlovss.workingcontacts.entity.Post

data class PostModel(
    val post: Post
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}