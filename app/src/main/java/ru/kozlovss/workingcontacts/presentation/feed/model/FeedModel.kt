package ru.kozlovss.workingcontacts.presentation.feed.model

import ru.kozlovss.workingcontacts.entity.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}