package ru.kozlovss.workingcontacts.presentation.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.presentation.post.model.PostModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postsRepository: PostRepository
): ViewModel() {

    private val _data = MutableStateFlow<Post?>(null)
    val data = _data.asStateFlow()

    private val _state = MutableStateFlow<PostModel.State>(PostModel.State.Idle)
    val state = _state.asStateFlow()

    fun updateData(id: Long?) = viewModelScope.launch {
        try {
            _state.value = PostModel.State.Loading
            if (id == null) {
                _state.value = PostModel.State.Error
            } else {
                _data.value = postsRepository.getById(id)
                _state.value = PostModel.State.Idle
            }
        } catch (e: Exception) {
            _state.value = PostModel.State.Error
            e.printStackTrace()
        }
    }

    fun clearData() {
        try {
            _data.value = null
        } catch (e: Exception) {
            _state.value = PostModel.State.Error
            e.printStackTrace()
        }
    }
}