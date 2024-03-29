package ru.kozlovss.workingcontacts.presentation.video.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kozlovss.workingcontacts.presentation.video.model.VideoModel
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(

): ViewModel() {

    private val _state = MutableStateFlow<VideoModel.State>(VideoModel.State.Idle)
    val state = _state.asStateFlow()

    fun setStateIde() {
        _state.value = VideoModel.State.Idle
    }

    fun setStateError() {
        _state.value = VideoModel.State.Error
    }

    fun setStateLoading() {
        _state.value = VideoModel.State.Loading
    }
}