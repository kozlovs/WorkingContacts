package ru.kozlovss.workingcontacts.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.entity.Event
import ru.kozlovss.workingcontacts.domain.usecases.GetEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikeEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.ParticipateEventByIdUseCase
import ru.kozlovss.workingcontacts.presentation.event.model.EventModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val likeEventByIdUseCase: LikeEventByIdUseCase,
    private val participateEventByIdUseCase: ParticipateEventByIdUseCase
) : ViewModel() {
    private val _data = MutableStateFlow<Event?>(null)
    val data = _data.asStateFlow()

    private val _state = MutableStateFlow<EventModel.State>(EventModel.State.Idle)
    val state = _state.asStateFlow()

    private val _speakersVisibility = MutableStateFlow(false)
    val speakersVisibility = _speakersVisibility.asStateFlow()

    fun updateData(id: Long?) = viewModelScope.launch {
        try {
            _state.value = EventModel.State.Loading
            if (id == null) {
                _state.value = EventModel.State.Error
            } else {
                _data.value = getEventByIdUseCase.execute(id)
                _state.value = EventModel.State.Idle
            }
        } catch (e: Exception) {
            _state.value = EventModel.State.Error
            e.printStackTrace()
        }
    }

    fun clearData() = viewModelScope.launch {
        try {
            _data.value = null
        } catch (e: Exception) {
            _state.value = EventModel.State.Error
            e.printStackTrace()
        }
    }

    fun likeById(id: Long?) = viewModelScope.launch {
        try {
            id?.let {
                likeEventByIdUseCase.execute(id)
                _data.value = getEvent(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventModel.State.Error
        }
    }

    fun participateById(id: Long?) = viewModelScope.launch {
        try {
            id?.let {
                participateEventByIdUseCase.execute(it)
                _data.value = getEvent(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventModel.State.Error
        }
    }

    fun switchSpeakersVisibility() {
        _speakersVisibility.value = !speakersVisibility.value
    }

    private suspend fun getEvent(id: Long) = getEventByIdUseCase.execute(id)
}