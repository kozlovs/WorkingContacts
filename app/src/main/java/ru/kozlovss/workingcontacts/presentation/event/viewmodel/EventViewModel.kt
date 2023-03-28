package ru.kozlovss.workingcontacts.presentation.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepository
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.presentation.event.model.EventModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _data = MutableStateFlow<Event?>(null)
    val data = _data.asStateFlow()

    private val _speakers = MutableStateFlow<List<User>>(emptyList())
    val speakers = _speakers.asStateFlow()


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
                _data.value = eventRepository.getById(id)
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
                eventRepository.likeById(it)
                _data.value = eventRepository.getById(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventModel.State.Error
        }
    }

    fun switchSpeakersVisibility() {
        _speakersVisibility.value = !speakersVisibility.value
    }

    fun getSpeakers(speakerIds: List<Long>) = viewModelScope.launch {
        _speakers.value = speakerIds.map { userRepository.getUserInfoById(it) }
    }
}