package ru.kozlovss.workingcontacts.presentation.userswall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    val postsData = MutableStateFlow<List<Post>>(emptyList())

    fun getPosts(id: Long) {
        viewModelScope.launch {
            postsData.value = wallRepository.getAll(id)
        }
    }

    val userData = userRepository.userData

    fun getUserData(userId: Long) = viewModelScope.launch {
        try {
            userRepository.getUserById(userId)
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