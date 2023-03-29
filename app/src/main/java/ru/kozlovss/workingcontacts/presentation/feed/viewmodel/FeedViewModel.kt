package ru.kozlovss.workingcontacts.presentation.feed.viewmodel

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
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.presentation.feed.model.FeedModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
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

    private val _state = MutableStateFlow<FeedModel.State>(FeedModel.State.Idle)
    val state = _state.asStateFlow()

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = FeedModel.State.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _state.value = FeedModel.State.Error
        }
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