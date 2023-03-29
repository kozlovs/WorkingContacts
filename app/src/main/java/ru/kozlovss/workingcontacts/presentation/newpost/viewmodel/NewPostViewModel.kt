package ru.kozlovss.workingcontacts.presentation.newpost.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.dto.PostRequest
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.presentation.newpost.model.NewPostModel
import java.io.File
import javax.inject.Inject

private var empty = Post(
    id = 0,
    authorId = 0L,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    published = "",
    coords = null,
    link = null,
    mentionedMe = false,
    likedByMe = false,
    attachment = null,
    ownedByMe = true,
    users = emptyMap()
)

@HiltViewModel
class NewPostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _postData = MutableStateFlow<Post?>(null)
    val postData = _postData.asStateFlow()

    private val _state =
        MutableStateFlow<NewPostModel.State>(NewPostModel.State.Idle)
    val state = _state.asStateFlow()

    private val _media = MutableStateFlow<MediaModel?>(null)
    val media = _media.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    val draftContent = MutableLiveData("")

    fun save(
        content: String,
        coord: Coordinates?,
        link: String?
    ) = viewModelScope.launch {
        try {
            _state.value = NewPostModel.State.Loading

            val postRequest = PostRequest(
                0,
                content,
                coord,
                link,
                null,
                emptyList()
            )
            Log.d("MyLog", "view model post $postRequest")
            repository.save(postRequest)
            clearData()
            _events.emit(Event.CreateNewItem)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = NewPostModel.State.Error
        }
//                photo.value?.let {
//                    repository.saveWithAttachment(post, it)
//                    clearPhoto()
//                } ?: repository.save(post)
    }

    fun clearDraft() {
        draftContent.value = ""
    }


    fun getData(id: Long) = viewModelScope.launch {
        _postData.value = repository.getById(id)
    }

    fun clearData() {
        _postData.value = null
        _state.value = NewPostModel.State.Idle
    }

    fun savePhoto(uri: Uri?, toFile: File?) {
        _media.value = MediaModel(uri, toFile, MediaModel.Type.PHOTO)
    }

    fun clearPhoto() {
        savePhoto(null, null)
    }



    sealed class Event {
        object CreateNewItem: Event()
        data class ShowSnackBar(val text: String): Event()
        data class ShowToast(val text: String): Event()
    }
}