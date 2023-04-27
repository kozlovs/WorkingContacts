package ru.kozlovss.workingcontacts.presentation.events.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.domain.usecases.GetEventsPagingDataUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikeEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.ParticipateEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RemoveEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.SwitchAudioUseCase
import ru.kozlovss.workingcontacts.presentation.events.model.EventsModel
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

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            likeEventByIdUseCase.execute(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventsModel.State.Error
        }
    }

    fun participateById(id: Long) = viewModelScope.launch {
        try {
            participateEventByIdUseCase.execute(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventsModel.State.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            removeEventByIdUseCase.execute(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventsModel.State.Error
        }
    }

    fun switchAudio(event: Event) = viewModelScope.launch {
        try {
            switchAudioUseCase.execute(event)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventsModel.State.Error
        }
    }
}