package ru.kozlovss.workingcontacts.presentation.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.domain.usecases.GetPostByIdUseCase
import ru.kozlovss.workingcontacts.domain.usecases.LikePostByIdUseCase
import ru.kozlovss.workingcontacts.presentation.post.model.PostModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val likePostByIdUseCase: LikePostByIdUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase
): ViewModel() {

    private val _data = MutableStateFlow<Post?>(null)
    val data = _data.asStateFlow()

    private val _state = MutableStateFlow<PostModel.State>(PostModel.State.Idle)
    val state = _state.asStateFlow()

    private val _mentionsVisibility = MutableStateFlow(false)
    val mentionsVisibility = _mentionsVisibility.asStateFlow()

    fun updateData(id: Long?) = viewModelScope.launch {
        try {
            _state.value = PostModel.State.Loading
            if (id == null) {
                _state.value = PostModel.State.Error
            } else {
                _data.value = getPost(id)
                _state.value = PostModel.State.Idle
            }
        } catch (e: Exception) {
            _state.value = PostModel.State.Error
            e.printStackTrace()
        }
    }

    fun clearData() = viewModelScope.launch {
        try {
            _data.value = null
        } catch (e: Exception) {
            _state.value = PostModel.State.Error
            e.printStackTrace()
        }
    }

    fun likeById(id: Long?) = viewModelScope.launch {
        try {
            id?.let {
                likePostByIdUseCase.execute(it)
                _data.value = getPost(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = PostModel.State.Error
        }
    }

    fun switchMentionsVisibility() {
        _mentionsVisibility.value = !mentionsVisibility.value
    }

    private suspend fun getPost(id: Long) = getPostByIdUseCase.execute(id)
}