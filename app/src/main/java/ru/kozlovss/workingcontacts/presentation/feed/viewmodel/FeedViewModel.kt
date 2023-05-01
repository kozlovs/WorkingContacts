package ru.kozlovss.workingcontacts.presentation.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.AuthError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import ru.kozlovss.workingcontacts.domain.error.UnknownError
import ru.kozlovss.workingcontacts.domain.usecases.GetFeedPostsPagingDataUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikePostByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RemovePostByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.SwitchAudioUseCase
import ru.kozlovss.workingcontacts.presentation.feed.model.FeedModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    getFeedPostsPagingDataUseCase: GetFeedPostsPagingDataUseCase,
    private val switchAudioUseCase: SwitchAudioUseCase,
    private val likePostByIdUseCase: LikePostByIdUseCase,
    private val removePostByIdUseCase: RemovePostByIdUseCase,
) : ViewModel() {

    val data = getFeedPostsPagingDataUseCase.execute()
    private val _state = MutableStateFlow<FeedModel.State>(FeedModel.State.Idle)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    fun likeById(id: Long) = viewModelScope.launch {
        catchExceptions {
            likePostByIdUseCase.execute(id)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            removePostByIdUseCase.execute(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = FeedModel.State.Error
        }
    }

    fun switchAudio(post: Post) = viewModelScope.launch {
        try {
            switchAudioUseCase.execute(post)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = FeedModel.State.Error
        }
    }

    private suspend inline fun <T, R> T.catchExceptions(block: (T) -> R) {
        try {
            block(this)
        } catch (e: ApiError) {
            e.printStackTrace()
            _events.emit(Event.ApiErrorMassage(e.reason))
        } catch (e: AuthError) {
            e.printStackTrace()
            _events.emit(Event.AuthErrorMassage)
        } catch (e: NetworkError) {
            e.printStackTrace()
            _events.emit(Event.NetworkErrorMassage)
        } catch (e: UnknownError) {
            e.printStackTrace()
            _events.emit(Event.UnknownErrorMassage)
        }
    }

    sealed class Event {
        class ApiErrorMassage(val message: String?) : Event()
        object AuthErrorMassage : Event()
        object NetworkErrorMassage : Event()
        object UnknownErrorMassage : Event()
    }
}