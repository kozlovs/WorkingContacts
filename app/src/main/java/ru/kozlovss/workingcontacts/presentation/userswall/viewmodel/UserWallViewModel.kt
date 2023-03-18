package ru.kozlovss.workingcontacts.presentation.userswall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.data.walldata.repository.UserWallRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.presentation.feed.model.FeedModel
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val wallRepository: UserWallRepository,
    private val appAuth: AppAuth,
    private val audioPlayer: AudioPlayer
) : ViewModel() {


    private val _postsData = MutableStateFlow<List<Post>>(emptyList())
    val postsData: StateFlow<List<Post>>
        get() = _postsData

    val userData = userRepository.userData

    private val _state = MutableStateFlow<FeedModel.FeedModelState>(FeedModel.FeedModelState.Idle)
    val state: StateFlow<FeedModel.FeedModelState>
        get() = _state

    fun getPosts(id: Long) {
        try {
            viewModelScope.launch {
                _state.value = FeedModel.FeedModelState.Refreshing
                _postsData.value = wallRepository.getAll(id)
                _state.value = FeedModel.FeedModelState.Idle
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = FeedModel.FeedModelState.Error
        }
    }

    fun cleanPosts() {
        _postsData.value = emptyList()
    }

    fun getUserData(userId: Long) = viewModelScope.launch {
        try {
            userRepository.getUserInfoById(userId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearUserData() = viewModelScope.launch {
        try {
            userRepository.clearUserInfo()
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

    fun isLogin() = appAuth.isAuthenticated()

    fun switchAudio(post: Post) {
        if (post.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(post.attachment)
        }
    }
}