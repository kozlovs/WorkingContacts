package ru.kozlovss.workingcontacts.presentation.userswall.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.data.walldata.repository.UserWallRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.presentation.feed.model.FeedModel
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val wallRepository: UserWallRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    var userId: Long? = null
        set(value) {
            value?.let { it ->
                wallRepository.userId = userId
                field = it
            } ?: {
                clearData()
                field = null
            }
        }

    private fun clearData() {
        wallRepository.clearData()
    }

    val data: Flow<PagingData<Post>> = wallRepository.posts
        .flowOn(Dispatchers.Default)

    val userData = userRepository.myData

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            wallRepository.likeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isLogin() = appAuth.isLogin()
}