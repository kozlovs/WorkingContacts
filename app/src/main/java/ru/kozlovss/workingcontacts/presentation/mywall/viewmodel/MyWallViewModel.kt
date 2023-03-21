package ru.kozlovss.workingcontacts.presentation.mywall.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.mywalldata.repository.MyWallRepository
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.userdata.dto.Token
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.util.SingleLiveEvent
import ru.kozlovss.workingcontacts.presentation.mywall.model.MyWallModel
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
class MyWallViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val wallRepository: MyWallRepository,
    private val appAuth: AppAuth,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val data: Flow<PagingData<Post>> = wallRepository.posts
        .flowOn(Dispatchers.Default)

    private val _myData = MutableStateFlow<User?>(null)
    val myData: StateFlow<User?>
        get() = _myData

    private val _state =
        MutableStateFlow<MyWallModel.MyWallModelState>(MyWallModel.MyWallModelState.Idle)
    val state: StateFlow<MyWallModel.MyWallModelState>
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

    fun updateMyData(token: Token?) = viewModelScope.launch {
        try {
            _state.value = MyWallModel.MyWallModelState.Loading
            if (token == null) {
                _state.value = MyWallModel.MyWallModelState.NoLogin
            } else {
                _myData.value = userRepository.getMyData(token.id)
                _state.value = MyWallModel.MyWallModelState.Idle
            }
        } catch (e: Exception) {
            _state.value = MyWallModel.MyWallModelState.Error
            e.printStackTrace()
        }
    }

    fun clearMyData() = viewModelScope.launch {
        try {
            _state.value = MyWallModel.MyWallModelState.Loading
            _myData.value = null
            _state.value = MyWallModel.MyWallModelState.NoLogin
        } catch (e: Exception) {
            _state.value = MyWallModel.MyWallModelState.Error
            e.printStackTrace()
        }
    }

    private fun save() {
        _edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    photo.value?.let {
                        wallRepository.saveWithAttachment(post, it)
                        clearPhoto()
                    } ?: wallRepository.save(post)
                } catch (e: Exception) {
                    _state.value = MyWallModel.MyWallModelState.Error
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

    fun clearPhoto() = savePhoto(null, null)

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            wallRepository.likeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = MyWallModel.MyWallModelState.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            wallRepository.removeById(id)
        } catch (e: Exception) {
            _state.value = MyWallModel.MyWallModelState.Error
        }
    }

    fun clearDraft() {
        draftContent.value = ""
    }

    fun savePhoto(uri: Uri?, toFile: File?) {
        _photo.value = PhotoModel(uri, toFile)
    }

    fun isLogin() = appAuth.isAuthenticated()

    fun switchAudio(post: Post) {
        if (post.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(post.attachment)
        }
    }
}