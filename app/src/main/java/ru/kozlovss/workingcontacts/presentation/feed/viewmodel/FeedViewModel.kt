package ru.kozlovss.workingcontacts.presentation.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.entity.Post
import ru.kozlovss.workingcontacts.domain.error.catchExceptions
import ru.kozlovss.workingcontacts.domain.usecases.CheckAuthUseCase
import ru.kozlovss.workingcontacts.domain.usecases.GetFeedPostsPagingDataUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikePostByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RemovePostByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.SwitchAudioUseCase
import ru.kozlovss.workingcontacts.presentation.feed.model.FeedModel
import ru.kozlovss.workingcontacts.presentation.util.EventMassage
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    getFeedPostsPagingDataUseCase: GetFeedPostsPagingDataUseCase,
    private val switchAudioUseCase: SwitchAudioUseCase,
    private val likePostByIdUseCase: LikePostByIdUseCase,
    private val removePostByIdUseCase: RemovePostByIdUseCase,
    private val checkAuthUseCase: CheckAuthUseCase
) : ViewModel() {

    val data = getFeedPostsPagingDataUseCase.execute()
    private val _state = MutableStateFlow<FeedModel.State>(FeedModel.State.Idle)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<EventMassage>()
    val events = _events.asSharedFlow()

    fun likeById(id: Long) = viewModelScope.launch {
        catchExceptions(_events) {
            likePostByIdUseCase.execute(id)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        catchExceptions(_events) {
            removePostByIdUseCase.execute(id)
        }
    }

    fun switchAudio(post: Post) = viewModelScope.launch {
        catchExceptions(_events) {
            switchAudioUseCase.execute(post)
        }
    }

    fun isLogin() = checkAuthUseCase.execute()
}