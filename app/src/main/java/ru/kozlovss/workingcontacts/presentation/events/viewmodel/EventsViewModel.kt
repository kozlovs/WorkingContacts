package ru.kozlovss.workingcontacts.presentation.events.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.presentation.events.model.EventsModel
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repository: EventRepository,
    appAuth: AppAuth,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Event>> = appAuth.authStateFlow
        .flatMapLatest { token ->
            repository.events
                .map { events ->
                    events.map { event ->
                        event.copy(ownedByMe = event.authorId == token?.id)
                    }
                }
        }.flowOn(Dispatchers.Default)

    private val _state =
        MutableStateFlow<EventsModel.EventsModelState>(EventsModel.EventsModelState.Idle)
    val state = _state.asStateFlow()

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventsModel.EventsModelState.Error
        }
    }

    fun participateById(id: Long) = viewModelScope.launch {
        try {
            repository.participateById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = EventsModel.EventsModelState.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _state.value = EventsModel.EventsModelState.Error
        }
    }

    fun switchAudio(event: Event) {
        if (event.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(event.attachment)
        }
    }
}