package ru.kozlovss.workingcontacts.presentation.auth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.entity.Attachment
import ru.kozlovss.workingcontacts.entity.MediaModel
import ru.kozlovss.workingcontacts.domain.usecases.AuthorizationUseCase
import ru.kozlovss.workingcontacts.domain.usecases.CheckAuthUseCase
import ru.kozlovss.workingcontacts.domain.usecases.GetTokenUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RegistrationUseCase
import ru.kozlovss.workingcontacts.presentation.auth.model.AuthModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    getTokenUseCase: GetTokenUseCase,
    private val checkAuthUseCase: CheckAuthUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val authorizationUseCase: AuthorizationUseCase
    ) : ViewModel() {

    val token = getTokenUseCase.execute()

    private val _avatar = MutableStateFlow<MediaModel?>(null)
    val avatar = _avatar.asStateFlow()

    private val _state = MutableStateFlow<AuthModel.State>(AuthModel.State.Idle)
    val state = _state.asStateFlow()

    fun isLogin() = checkAuthUseCase.execute()

    fun logIn(login: String, password: String) = viewModelScope.launch {
        _state.value = AuthModel.State.Loading
        authorizationUseCase.execute(login, password)
        _state.value = AuthModel.State.Idle
    }

    fun register(login: String, password: String, name: String) = viewModelScope.launch {
        _state.value = AuthModel.State.Loading
        registrationUseCase.execute(login, password, name, avatar.value)
        clearAvatar()
        _state.value = AuthModel.State.Idle
    }

    fun saveAvatar(uri: Uri?, toFile: File?) = viewModelScope.launch {
        _avatar.value = MediaModel(uri, toFile, Attachment.Type.IMAGE)
    }

    fun clearAvatar() = viewModelScope.launch {
        _avatar.value = null
    }
}