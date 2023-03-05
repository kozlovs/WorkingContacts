package ru.kozlovss.workingcontacts.presentation.mywall.viewmodel

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.mywalldata.repository.MyWallRepository
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.util.DialogManager
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
class MyWallViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val wallRepository: MyWallRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    val authState = appAuth.authStateFlow

    val data: Flow<PagingData<Post>> = wallRepository.posts
        .flowOn(Dispatchers.Default)

    val userData = userRepository.myData

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
                        wallRepository.saveWithAttachment(post, it)
                        clearPhoto()
                    } ?: wallRepository.save(post)
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
            wallRepository.likeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = FeedModel.FeedModelState.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            wallRepository.removeById(id)
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

    private fun isLogin() = appAuth.authStateFlow.value.id != 0L

    fun checkLogin(fragment: Fragment): Boolean =
        if (isLogin()) {
            true
        } else {
            DialogManager.errorAuthDialog(fragment)
            false
        }
}