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
import ru.kozlovss.workingcontacts.domain.usecases.LogOutUseCase
import ru.kozlovss.workingcontacts.domain.usecases.RegistrationUseCase
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    getTokenUseCase: GetTokenUseCase,
    private val checkAuthUseCase: CheckAuthUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val authorizationUseCase: AuthorizationUseCase,
    private val logOutUseCase: LogOutUseCase
) : ViewModel() {

    val token = getTokenUseCase.execute()

    private val _avatar = MutableStateFlow<MediaModel?>(null)
    val avatar = _avatar.asStateFlow()

    fun isLogin() = checkAuthUseCase.execute()

    fun logIn(login: String, password: String) = viewModelScope.launch {
        authorizationUseCase.execute(login, password)
    }

    fun register(login: String, password: String, name: String) = viewModelScope.launch {
        registrationUseCase.execute(login, password, name, avatar.value)
        clearAvatar()
    }

    fun logout() = viewModelScope.launch {
        logOutUseCase.execute()
    }

    fun saveAvatar(uri: Uri?, toFile: File?) = viewModelScope.launch {
        _avatar.value = MediaModel(uri, toFile, Attachment.Type.IMAGE)
    }

    fun clearAvatar() = viewModelScope.launch {
        _avatar.value = null
    }
}