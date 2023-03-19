package ru.kozlovss.workingcontacts.presentation.mywall.model

import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

data class MyWallModel(
    val posts: List<Post> = emptyList(),
    val user: User?,
    val empty: Boolean = false,
) {
    sealed interface MyWallModelState {
        object Idle : MyWallModelState
        object Error : MyWallModelState
        object Loading : MyWallModelState
        object NoLogin : MyWallModelState
    }
}