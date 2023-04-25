package ru.kozlovss.workingcontacts.presentation.mywall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.data.mywalldata.repository.MyWallRepository
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.userdata.dto.Token
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.usecases.LikePostByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RemovePostByIdUseCase
import ru.kozlovss.workingcontacts.presentation.mywall.model.MyWallModel
import javax.inject.Inject


@HiltViewModel
class MyWallViewModel @Inject constructor(
    private val userRepository: UserRepository,
    wallRepository: MyWallRepository,
    private val jobRepository: JobRepository,
    private val appAuth: AppAuth,
    private val audioPlayer: AudioPlayer,
    private val likePostByIdUseCase: LikePostByIdUseCase,
    private val removePostByIdUseCase: RemovePostByIdUseCase
) : ViewModel() {

    val postData: Flow<PagingData<Post>> = wallRepository.posts
        .flowOn(Dispatchers.Default)

    private val _jobsData = MutableStateFlow<List<Job>>(emptyList())
    val jobsData = _jobsData.asStateFlow()

    private val _myData = MutableStateFlow<User?>(null)
    val myData = _myData.asStateFlow()

    private val _state =
        MutableStateFlow<MyWallModel.State>(MyWallModel.State.Idle)
    val state = _state.asStateFlow()

    fun getJobs() = viewModelScope.launch {
        try {
            _state.value = MyWallModel.State.RefreshingJobs
            _jobsData.value = jobRepository.getMyJobs()
            _state.value = MyWallModel.State.Idle
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = MyWallModel.State.Error
        }
    }

    fun updateMyData(token: Token?) = viewModelScope.launch {
        try {
            _state.value = MyWallModel.State.Loading
            if (token == null) {
                _state.value = MyWallModel.State.NoLogin
            } else {
                _myData.value = userRepository.getMyData(token.id)
                _state.value = MyWallModel.State.Idle
            }
        } catch (e: Exception) {
            _state.value = MyWallModel.State.Error
            e.printStackTrace()
        }
    }

    fun clearMyData() = viewModelScope.launch {
        try {
            _state.value = MyWallModel.State.Loading
            _myData.value = null
            _jobsData.value = emptyList()
            _state.value = MyWallModel.State.NoLogin
        } catch (e: Exception) {
            _state.value = MyWallModel.State.Error
            e.printStackTrace()
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            likePostByIdUseCase.execute(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = MyWallModel.State.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            removePostByIdUseCase.execute(id)
        } catch (e: Exception) {
            _state.value = MyWallModel.State.Error
        }
    }

    fun isLogin() = appAuth.isAuthenticated()

    fun switchAudio(post: Post) {
        if (post.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(post.attachment)
        }
    }

    fun removeJobById(id: Long) = viewModelScope.launch {
        jobRepository.removeJobById(id)
        _jobsData.value = jobRepository.getMyJobs()
    }
}