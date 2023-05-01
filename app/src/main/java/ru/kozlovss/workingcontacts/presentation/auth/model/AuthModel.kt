package ru.kozlovss.workingcontacts.presentation.auth.model

import ru.kozlovss.workingcontacts.entity.Post

data class AuthModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}