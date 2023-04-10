package ru.kozlovss.workingcontacts.presentation.newpost.viewmodel

import android.net.Uri
import android.util.Log
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
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.postsdata.dto.PostRequest
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.presentation.newpost.model.NewPostModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NewPostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<NewPostModel.State>(NewPostModel.State.Idle)
    val state = _state.asStateFlow()

    private val _postId = MutableStateFlow<Long?>(null)
    val postId = _postId.asStateFlow()

    private val _content = MutableStateFlow<String?>(null)
    val content = _content.asStateFlow()

    private val _link = MutableStateFlow<String?>(null)
    val link = _link.asStateFlow()

    private val _coordinates = MutableStateFlow<Coordinates?>(null)
    val coordinates = _coordinates.asStateFlow()

    private val _attachment = MutableStateFlow<MediaModel?>(null)
    val attachment = _attachment.asStateFlow()

    private val _attachmentRemote = MutableStateFlow<Attachment?>(null)
    val attachmentRemote = _attachmentRemote.asStateFlow()

    private val _mentions = MutableStateFlow<List<User>>(emptyList())
    val mentions = _mentions.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    fun save(
        content: String,
        coords: Coordinates?,
        link: String?
    ) = viewModelScope.launch {
        try {
            _state.value = NewPostModel.State.Loading
            val mentionIds = mentions.value.let { users ->
                users.map { it.id }
            }

            val postRequest = PostRequest(
                id = postId.value ?: 0,
                content = content,
                coords = coords,
                link = link,
                attachment = attachmentRemote.value,
                mentionIds = mentionIds
            )
            Log.d("MyLog", "viewModel request $postRequest")
            repository.save(postRequest, attachment.value)
            clearData()
            _events.emit(Event.CreateNewItem)
            _state.value = NewPostModel.State.Idle
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = NewPostModel.State.Error
        }
    }

    fun makeDraft(
        content: String?,
        coord: Coordinates?,
        link: String?
    ) = viewModelScope.launch {
        _coordinates.value = coord
        _content.value = content
        _link.value = link
    }

    fun getData(id: Long) = viewModelScope.launch {
        _state.value = NewPostModel.State.Loading
        try {
            val post = repository.getById(id)
            post?.let {
                _postId.value = it.id
                _content.value = it.content
                it.link?.let { link ->
                    _link.value = link
                }
                it.attachment?.let { attachment ->
                    _attachmentRemote.value = attachment
                }
                if (it.mentionIds.isNotEmpty()) {
                    _mentions.value = it.mentionIds.map { id ->
                        userRepository.getUserInfoById(id)
                    }
                }
            }
            _state.value = NewPostModel.State.Idle
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = NewPostModel.State.Error
        }
    }

    fun clearData() = viewModelScope.launch {
        _postId.value = null
        _content.value = null
        _link.value = null
        _coordinates.value = null
        _attachment.value = null
        _attachmentRemote.value = null
        _mentions.value = emptyList()
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
    fun addMention(user: User) = viewModelScope.launch {
        if (!_mentions.value.contains(user)) {
            _mentions.value = mentions.value.plus(user)
            _events.emit(Event.ShowToast("Added mention of ${user.name}"))
        }
    }

    fun removeMention(user: User) = viewModelScope.launch {
        _mentions.value = mentions.value.minus(user)
    }


    sealed class Event {
        object CreateNewItem : Event()
        data class ShowSnackBar(val text: String) : Event()
        data class ShowToast(val text: String) : Event()
    }
}