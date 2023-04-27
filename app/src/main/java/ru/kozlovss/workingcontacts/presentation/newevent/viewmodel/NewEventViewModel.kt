package ru.kozlovss.workingcontacts.presentation.newevent.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.data.eventsdata.dto.EventRequest
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.domain.usecases.GetEventByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.GetUserByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.SaveEventUseCase
import ru.kozlovss.workingcontacts.presentation.newevent.model.NewEventModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NewEventViewModel @Inject constructor(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val saveEventUseCase: SaveEventUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<NewEventModel.State>(NewEventModel.State.Idle)
    val state = _state.asStateFlow()

    private val _eventId = MutableStateFlow<Long?>(null)
    val eventId = _eventId.asStateFlow()

    private val _content = MutableStateFlow<String?>(null)
    val content = _content.asStateFlow()

    private val _dateTime = MutableStateFlow<String?>(null)
    val dateTime = _dateTime.asStateFlow()

    private val _type = MutableStateFlow(Event.Type.ONLINE)
    val type = _type.asStateFlow()

    private val _link = MutableStateFlow<String?>(null)
    val link = _link.asStateFlow()

    private val _coordinates = MutableStateFlow<Coordinates?>(null)
    val coordinates = _coordinates.asStateFlow()

    private val _attachment = MutableStateFlow<MediaModel?>(null)
    val attachment = _attachment.asStateFlow()

    private val _attachmentRemote = MutableStateFlow<Attachment?>(null)
    val attachmentRemote = _attachmentRemote.asStateFlow()

    private val _speakers = MutableStateFlow<List<User>>(emptyList())
    val speakers = _speakers.asStateFlow()

    private val _events = MutableSharedFlow<LocalEvent>()
    val events = _events.asSharedFlow()

    fun save(
        content: String,
        dateTime: String?,
        coords: Coordinates?,
        link: String?
    ) = viewModelScope.launch {
        try {
            _state.value = NewEventModel.State.Loading
            val speakerIds = speakers.value.let { users ->
                users.map { it.id }
            }

            val eventRequest = EventRequest(
                id = eventId.value ?: 0,
                content = content,
                datetime = dateTime,
                coords = coords,
                type = type.value,
                attachment = attachmentRemote.value,
                link = link,
                speakerIds = speakerIds
            )
            saveEventUseCase.execute(eventRequest, attachment.value)
            clearData()
            _events.emit(LocalEvent.CreateNewItem)
            _state.value = NewEventModel.State.Idle
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = NewEventModel.State.Error
        }
    }

    fun makeDraft(
        content: String?,
        dataTime: String?,
        coords: Coordinates?,
        link: String?
    ) = viewModelScope.launch {
        _coordinates.value = coords
        _dateTime.value = dataTime
        _content.value = content
        _link.value = link
    }

    fun getData(id: Long) = viewModelScope.launch {
        _state.value = NewEventModel.State.Loading
        try {
            getEventByIdUseCase.execute(id).let {
                _eventId.value = it.id
                _content.value = it.content
                _dateTime.value = it.datetime
                _type.value = it.type
                it.link?.let { link ->
                    _link.value = link
                }
                it.attachment?.let { attachment ->
                    _attachmentRemote.value = attachment
                }
                if (it.speakerIds.isNotEmpty()) {
                    _speakers.value = it.speakerIds.map { id ->
                        getUserByIdUseCase.execute(id)
                    }
                }
            }
            _state.value = NewEventModel.State.Idle
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = NewEventModel.State.Error
        }
    }

    fun clearData() = viewModelScope.launch {
        _eventId.value = null
        _content.value = null
        _dateTime.value = null
        _link.value = null
        _type.value = Event.Type.ONLINE
        _coordinates.value = null
        _attachment.value = null
        _attachmentRemote.value = null
        _speakers.value = emptyList()
    }

    fun saveAttachment(uri: Uri?, toFile: File?, type: Attachment.Type) = viewModelScope.launch {
        _attachmentRemote.value = null
        _attachment.value = MediaModel(uri, toFile, type)
    }

    fun clearAttachment() = viewModelScope.launch {
        _attachment.value = null
        _attachmentRemote.value = null
    }

    fun setCoordinates(coordinates: Coordinates) = viewModelScope.launch {
        _coordinates.value = coordinates
    }

    fun setType(type: Event.Type) {
        _type.value = type
    }

    fun addSpeaker(user: User) = viewModelScope.launch {
        if (!_speakers.value.contains(user)) {
            _speakers.value = speakers.value.plus(user)
            _events.emit(LocalEvent.AddedSpeaker(user.name))
        }
    }

    fun removeSpeaker(user: User) = viewModelScope.launch {
        _speakers.value = speakers.value.minus(user)
    }

    sealed class LocalEvent {
        object CreateNewItem : LocalEvent()
        data class AddedSpeaker(val userName: String) : LocalEvent()
    }
}