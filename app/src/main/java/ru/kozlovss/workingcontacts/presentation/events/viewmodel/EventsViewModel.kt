package ru.kozlovss.workingcontacts.presentation.events.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.usecases.GetEventsPagingDataUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikeEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.ParticipateEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RemoveEventByIdUseCase
import ru.kozlovss.workingcontacts.presentation.events.model.EventsModel
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    getEventsPagingDataUseCase: GetEventsPagingDataUseCase,
    private val likeEventByIdUseCase: LikeEventByIdUseCase,
    private val participateEventByIdUseCase: ParticipateEventByIdUseCase,
    private val removeEventByIdUseCase: RemoveEventByIdUseCase
) : ViewModel() {

    val data: Flow<PagingData<Event>> = getEventsPagingDataUseCase.execute()

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
            _state.value = EventsModel.State.Error
        }
    }

    fun switchAudio(event: Event) {
        if (event.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(event.attachment)
        }
    }
}