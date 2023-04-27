package ru.kozlovss.workingcontacts.presentation.userswall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.walldata.repository.UserWallRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.usecases.GetUserByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikePostByIdUseCase
import ru.kozlovss.workingcontacts.presentation.userswall.model.UserWallModel
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val wallRepository: UserWallRepository,
    private val jobRepository: JobRepository,
    private val appAuth: AppAuth,
    private val audioPlayer: AudioPlayer,
    private val likePostByIdUseCase: LikePostByIdUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    private val _postsData = MutableStateFlow<List<Post>>(emptyList())
    val postsData = _postsData.asStateFlow()

    private val _jobsData = MutableStateFlow<List<Job>>(emptyList())
    val jobsData = _jobsData.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData = _userData.asStateFlow()

    private val _state = MutableStateFlow<UserWallModel.State>(UserWallModel.State.Idle)
    val state = _state.asStateFlow()

    fun getPosts(id: Long? = null) {
        try {
            viewModelScope.launch {
                _postsData.value = if (id != null) {
                    _state.value = UserWallModel.State.Loading
                    wallRepository.getAll(id)
                } else if (userData.value != null) {
                    _state.value = UserWallModel.State.RefreshingPosts
                    wallRepository.getAll(userData.value!!.id)
                } else {
                    emptyList()
                }
                _state.value = UserWallModel.State.Idle
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = UserWallModel.State.Error
        }
    }

    private fun clearPosts() {
        _postsData.value = emptyList()
    }

    fun getJobs(id: Long? = null) {
        try {
            viewModelScope.launch {
                _jobsData.value = if (id != null) {
                    jobRepository.getJobsByUserId(id)
                } else if (userData.value != null) {
                    _state.value = UserWallModel.State.RefreshingJobs
                    jobRepository.getJobsByUserId(userData.value!!.id)
                } else {
                    emptyList()
                }
                _state.value = UserWallModel.State.Idle
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = UserWallModel.State.Error
        }
    }

    private fun clearJobs() {
        _jobsData.value = emptyList()
    }

    private fun getUserData(userId: Long) = viewModelScope.launch {
        try {
            _userData.value = getUserByIdUseCase.execute(userId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearUserData() = viewModelScope.launch {
        try {
            _userData.value = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearData() {
        clearJobs()
        clearPosts()
        clearUserData()
    }


    fun likeById(id: Long) = viewModelScope.launch {
        try {
            userData.value?.let {
                likePostByIdUseCase.execute(id)
                _postsData.value = wallRepository.getAll(it.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = UserWallModel.State.Error
        }
    }

    fun isLogin() = appAuth.isAuthenticated()

    fun switchAudio(post: Post) {
        if (post.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(post.attachment)
        }
    }

    fun getData(userId: Long) {
        _state.value = UserWallModel.State.Loading
        getUserData(userId)
        getPosts(userId)
        getJobs(userId)
    }
}