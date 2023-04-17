package ru.kozlovss.workingcontacts.presentation.mywall.model

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.userdata.dto.User

data class MyWallModel(
    val posts: List<Post> = emptyList(),
    val user: User? = null
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
        object NoLogin : State
        object RefreshingJobs : State
    }
}