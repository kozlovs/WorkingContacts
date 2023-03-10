package ru.kozlovss.workingcontacts.presentation.events.viewmodel

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepository
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.domain.util.SingleLiveEvent
import ru.kozlovss.workingcontacts.presentation.events.model.EventsModel
import java.io.File
import javax.inject.Inject


private var empty = Event(
    id = 0,
    authorId = 0L,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    datetime = "",
    published = "",
    coords = null,
    type = Event.Type.ONLINE,
    likedByMe = false,
    participatedByMe = false,
    attachment = null,
    link = null,
    ownedByMe = true,
    users = emptyMap()
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val appAuth: AppAuth,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val authState = appAuth.authStateFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Event>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.events
                .map { events ->
                    events.map { event ->
                        event.copy(ownedByMe = event.authorId == myId)
                    }
                }
        }.flowOn(Dispatchers.Default)

    private val _state =
        MutableStateFlow<EventsModel.EventsModelState>(EventsModel.EventsModelState.Idle)
    val state: StateFlow<EventsModel.EventsModelState>
        get() = _state

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _photo = MutableStateFlow<PhotoModel?>(null)
    val photo: StateFlow<PhotoModel?>
        get() = _photo

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Event>
        get() = _edited


    val draftContent = MutableLiveData("")

    fun changeContentAndSave(content: String) {
        changeContent(content)
        save()
    }

    private fun changeContent(content: String) {
        val text = content.trim()
        _edited.value?.let {
            if (it.content != text) {
                _edited.value = it.copy(
                    content = text
                )
            }
        }
    }

    private fun save() {
        _edited.value?.let { event ->
            _eventCreated.value = Unit
            viewModelScope.launch {
                try {
                    photo.value?.let {
                        repository.saveWithAttachment(event, it)
                        clearPhoto()
                    } ?: repository.save(event)
                } catch (e: Exception) {
                    _state.value = EventsModel.EventsModelState.Error
                }
            }
        }
        clearEdited()
    }

    fun edit(event: Event) {
        _edited.value = event
    }

    fun clearEdited() {
        _edited.value = empty
    }

    fun clearPhoto() {
        savePhoto(null, null)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
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

    fun clearDraft() {
        draftContent.value = ""
    }

    fun savePhoto(uri: Uri?, toFile: File?) {
        _photo.value = PhotoModel(uri, toFile)
    }

    private fun isLogin() = appAuth.authStateFlow.value.id != 0L

    suspend fun getById(id: Long) = repository.getById(id)

    fun checkLogin(fragment: Fragment): Boolean =
        if (isLogin()) {
            true
        } else {
            DialogManager.errorAuthDialog(fragment)
            false
        }

    fun switchAudio(event: Event) {
        if (event.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(event.attachment)
        }
    }
}