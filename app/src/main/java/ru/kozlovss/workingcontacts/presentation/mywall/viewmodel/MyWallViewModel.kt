package ru.kozlovss.workingcontacts.presentation.mywall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.entity.Job
import ru.kozlovss.workingcontacts.entity.Post
import ru.kozlovss.workingcontacts.entity.Token
import ru.kozlovss.workingcontacts.entity.User
import ru.kozlovss.workingcontacts.domain.usecases.CheckAuthUseCase
import ru.kozlovss.workingcontacts.domain.usecases.GetMyJobsUseCase
import ru.kozlovss.workingcontacts.domain.usecases.GetMyWallPostsPagingDataUseCase
import ru.kozlovss.workingcontacts.domain.usecases.GetUserByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikePostByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LogOutUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RemoveJobByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RemovePostByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.SwitchAudioUseCase
import ru.kozlovss.workingcontacts.presentation.mywall.model.MyWallModel
import javax.inject.Inject


@HiltViewModel
class MyWallViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getMyJobsUseCase: GetMyJobsUseCase,
    private val removeJobByIdUseCase: RemoveJobByIdUseCase,
    private val checkAuthUseCase: CheckAuthUseCase,
    private val switchAudioUseCase: SwitchAudioUseCase,
    private val likePostByIdUseCase: LikePostByIdUseCase,
    private val removePostByIdUseCase: RemovePostByIdUseCase,
    private val logOutUseCase: LogOutUseCase,
    getMyWallPostsPagingDataUseCase: GetMyWallPostsPagingDataUseCase
) : ViewModel() {

    val postData = getMyWallPostsPagingDataUseCase.execute()

    private val _jobsData = MutableStateFlow<List<Job>>(emptyList())
    val jobsData = _jobsData.asStateFlow()

    private val _myData = MutableStateFlow<User?>(null)
    val myData = _myData.asStateFlow()

    private val _state = MutableStateFlow<MyWallModel.State>(MyWallModel.State.Idle)
    val state = _state.asStateFlow()

    fun getJobs() = viewModelScope.launch {
        try {
            _state.value = MyWallModel.State.RefreshingJobs
            _jobsData.value = getMyJobsUseCase.execute()
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
                _myData.value = getUserByIdUseCase.execute(token.id)
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

    fun isLogin() = checkAuthUseCase.execute()

    fun switchAudio(post: Post) = viewModelScope.launch {
        switchAudioUseCase.execute(post)
    }

    fun removeJobById(id: Long) = viewModelScope.launch {
        removeJobByIdUseCase.execute(id)
        _jobsData.value = getMyJobsUseCase.execute()
    }

    fun logout() = viewModelScope.launch {
        logOutUseCase.execute()
    }
}