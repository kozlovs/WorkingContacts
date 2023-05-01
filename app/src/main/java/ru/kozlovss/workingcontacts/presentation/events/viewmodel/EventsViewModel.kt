package ru.kozlovss.workingcontacts.presentation.events.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.domain.error.catchExceptions
import ru.kozlovss.workingcontacts.entity.Event
import ru.kozlovss.workingcontacts.domain.usecases.GetEventsPagingDataUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikeEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.ParticipateEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RemoveEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.SwitchAudioUseCase
import ru.kozlovss.workingcontacts.presentation.events.model.EventsModel
import ru.kozlovss.workingcontacts.presentation.util.EventMassage
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    getEventsPagingDataUseCase: GetEventsPagingDataUseCase,
    private val likeEventByIdUseCase: LikeEventByIdUseCase,
    private val participateEventByIdUseCase: ParticipateEventByIdUseCase,
    private val removeEventByIdUseCase: RemoveEventByIdUseCase,
    private val switchAudioUseCase: SwitchAudioUseCase
) : ViewModel() {

    val data = getEventsPagingDataUseCase.execute()

    private val _state = MutableStateFlow<EventsModel.State>(EventsModel.State.Idle)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<EventMassage>()
    val events = _events.asSharedFlow()

    fun likeById(id: Long) = viewModelScope.launch {
        catchExceptions(_events) {
            likeEventByIdUseCase.execute(id)
        }
    }

    fun participateById(id: Long) = viewModelScope.launch {
        catchExceptions(_events) {
            participateEventByIdUseCase.execute(id)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        catchExceptions(_events) {
            removeEventByIdUseCase.execute(id)
        }
    }

    fun switchAudio(event: Event) = viewModelScope.launch {
        catchExceptions(_events) {
            switchAudioUseCase.execute(event)
        }
    }
}