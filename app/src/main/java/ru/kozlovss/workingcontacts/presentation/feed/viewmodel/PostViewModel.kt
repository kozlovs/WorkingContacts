package ru.kozlovss.workingcontacts.presentation.feed.viewmodel

import android.net.Uri
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
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.util.SingleLiveEvent
import ru.kozlovss.workingcontacts.presentation.feed.model.FeedModel
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
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val authState = appAuth.authStateFlow
    val audioPlayerState = audioPlayer.isPlaying

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { token ->
            repository.posts
                .map { posts ->
                    posts.map { post ->
                        post.copy(ownedByMe = post.authorId == token?.id)
                    }
                }
        }.flowOn(Dispatchers.Default)

    private val _state = MutableStateFlow<FeedModel.FeedModelState>(FeedModel.FeedModelState.Idle)
    val state: StateFlow<FeedModel.FeedModelState>
        get() = _state

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableStateFlow<PhotoModel?>(null)
    val photo: StateFlow<PhotoModel?>
        get() = _photo

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post>
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
        _edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    photo.value?.let {
                        repository.saveWithAttachment(post, it)
                        clearPhoto()
                    } ?: repository.save(post)
                } catch (e: Exception) {
                    _state.value = FeedModel.FeedModelState.Error
                }
            }
        }
        clearEdited()
    }

    fun edit(post: Post) {
        _edited.value = post
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
            _state.value = FeedModel.FeedModelState.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _state.value = FeedModel.FeedModelState.Error
        }
    }

    fun clearDraft() {
        draftContent.value = ""
    }

    fun savePhoto(uri: Uri?, toFile: File?) {
        _photo.value = PhotoModel(uri, toFile)
    }

    suspend fun getById(id: Long) = repository.getById(id)

    fun switchAudio(post: Post) {
        if (post.attachment?.type == Attachment.Type.AUDIO) {
            viewModelScope.launch {
                audioPlayer.switch(post.attachment)
                repository.switchAudioPlayer(post, audioPlayerState.value)
            }
        }
    }

    fun stopAudio() {
        viewModelScope.launch {
            repository.stopAudioPlayer()
        }
    }
}