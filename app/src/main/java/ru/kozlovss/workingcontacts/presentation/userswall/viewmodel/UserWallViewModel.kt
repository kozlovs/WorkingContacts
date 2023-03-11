package ru.kozlovss.workingcontacts.presentation.userswall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.data.walldata.repository.UserWallRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
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
                field = it
                getUserData()
            } ?: {
                field = null
                clearData()
            }
        }


    private fun clearData() {
        wallRepository.clearData()
        userRepository.clearUserInfo()
    }

    val data: Flow<PagingData<Post>> = wallRepository.posts
        .flowOn(Dispatchers.Default)

    val userData = userRepository.userData

    private fun getUserData() = viewModelScope.launch {
        try {
            userId?.let {
                userRepository.getUserById(it)
                wallRepository.getUserPosts(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun likeById(id: Long) = viewModelScope.launch {
        try {
            wallRepository.likeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isLogin() = appAuth.isLogin()
}