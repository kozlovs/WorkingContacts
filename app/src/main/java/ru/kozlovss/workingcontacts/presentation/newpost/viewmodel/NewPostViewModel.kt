package ru.kozlovss.workingcontacts.presentation.newpost.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
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

    private val _mentions = MutableStateFlow<List<User>?>(emptyList())
    val mentions = _mentions.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    fun save(
        content: String,
        coord: Coordinates?,
        link: String?
    ) = viewModelScope.launch {
        try {
            _state.value = NewPostModel.State.Loading
            val mentionIds = mentions.value?.let { users ->
                users.map { it.id }
            }

            val postRequest = PostRequest(
                0,
                content,
                coord,
                link,
                null,
                mentionIds.takeIf { it != null }
            )
            Log.d("MyLog", "viewModel request $postRequest")
            repository.save(postRequest, attachment.value)
            clearData()
            _events.emit(Event.CreateNewItem)
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
            post?.let { post ->
                _postId.value = post.id
                _content.value = post.content
                post.link?.let { _link.value = it }
                post.attachment?.let {
                    _attachment.value = MediaModel(it.url.toUri(), null, it.type)
                }
                if (post.mentionIds.isNotEmpty()) {
                    _mentions.value = post.mentionIds.map {
                        userRepository.getUserInfoById(it)
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
        _state.value = NewPostModel.State.Idle
    }

    fun saveAttachment(uri: Uri?, toFile: File?, type: Attachment.Type) = viewModelScope.launch {
        _attachment.value = MediaModel(uri, toFile, type)
    }

    fun clearAttachment() = viewModelScope.launch {
        _attachment.value = null
    }

    fun setCoordinates(coordinates: Coordinates) = viewModelScope.launch {
        _coordinates.value = coordinates
    }

    fun clearCoordinates() = viewModelScope.launch {
        _coordinates.value = null
    }


    sealed class Event {
        object CreateNewItem : Event()
        data class ShowSnackBar(val text: String) : Event()
        data class ShowToast(val text: String) : Event()
    }
}