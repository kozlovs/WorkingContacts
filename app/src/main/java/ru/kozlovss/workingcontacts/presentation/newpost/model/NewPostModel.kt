package ru.kozlovss.workingcontacts.presentation.newpost.model

import ru.kozlovss.workingcontacts.entity.Post

class NewPostModel(
    val post: Post
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}