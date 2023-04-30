package ru.kozlovss.workingcontacts.presentation.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
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
        try {
            likePostByIdUseCase.execute(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _events.emit(Event.ErrorMassage(e.message))
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

    sealed class Event {
        data class ErrorMassage (val message: String?) : Event()
    }
}