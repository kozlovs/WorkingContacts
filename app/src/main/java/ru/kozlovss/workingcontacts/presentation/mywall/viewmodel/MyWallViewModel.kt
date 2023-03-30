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
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
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
    private val jobRepository: JobRepository,
    private val appAuth: AppAuth,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val postData: Flow<PagingData<Post>> = wallRepository.posts
        .flowOn(Dispatchers.Default)

    private val _jobsData = MutableStateFlow<List<Job>>(emptyList())
    val jobsData = _jobsData.asStateFlow()

    private val _myData = MutableStateFlow<User?>(null)
    val myData = _myData.asStateFlow()

    private val _state =
        MutableStateFlow<MyWallModel.State>(MyWallModel.State.Idle)
    val state = _state.asStateFlow()

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableStateFlow<MediaModel?>(null)
    val photo = _photo.asStateFlow()

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post>
        get() = _edited

    val draftContent = MutableLiveData("")

    fun getJobs() = viewModelScope.launch {
        try {
            _state.value = MyWallModel.State.RefreshingJobs
            _jobsData.value = jobRepository.getMyJobs()
            _state.value = MyWallModel.State.Idle
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = MyWallModel.State.Error
        }
    }

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

    private fun save() = viewModelScope.launch {
        _edited.value?.let { post ->
            _postCreated.value = Unit
            try {
                photo.value?.let {
                    wallRepository.saveWithAttachment(post, it)
                    clearPhoto()
                } ?: wallRepository.save(post)
            } catch (e: Exception) {
                _state.value = MyWallModel.State.Error
            }
        }
        clearEdited()
    }
    fun updateMyData(token: Token?) = viewModelScope.launch {
        try {
            _state.value = MyWallModel.State.Loading
            if (token == null) {
                _state.value = MyWallModel.State.NoLogin
            } else {
                _myData.value = userRepository.getMyData(token.id)
                _state.value = MyWallModel.State.Idle
            }
        } catch (e: Exception) {
            _state.value = MyWallModel.State.Error
            e.printStackTrace()
        }
    }

    fun clearMyData() = viewModelScope.launch {
        try {
            _state.value = MyWallModel.State.Loading
            _myData.value = null
            _jobsData.value = emptyList()
            _state.value = MyWallModel.State.NoLogin
        } catch (e: Exception) {
            _state.value = MyWallModel.State.Error
            e.printStackTrace()
        }
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
            _state.value = MyWallModel.State.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            wallRepository.removeById(id)
        } catch (e: Exception) {
            _state.value = MyWallModel.State.Error
        }
    }

    fun clearDraft() {
        draftContent.value = ""
    }

    fun savePhoto(uri: Uri?, toFile: File?) {
        _photo.value = MediaModel(uri, toFile, Attachment.Type.IMAGE)
    }

    fun isLogin() = appAuth.isAuthenticated()

    fun switchAudio(post: Post) {
        if (post.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(post.attachment)
        }
    }

    fun removeJobById(id: Long) = viewModelScope.launch {
        jobRepository.removeJobById(id)
        _jobsData.value = jobRepository.getMyJobs()
    }
}