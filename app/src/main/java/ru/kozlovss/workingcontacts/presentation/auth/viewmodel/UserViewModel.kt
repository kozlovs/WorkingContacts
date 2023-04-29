package ru.kozlovss.workingcontacts.presentation.auth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.usecases.CheckAuthUseCase
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val appAuth: AppAuth,
    private val checkAuthUseCase: CheckAuthUseCase
) : ViewModel() {

    val token = appAuth.authStateFlow

    private val _avatar = MutableStateFlow<MediaModel?>(null)
    val avatar = _avatar.asStateFlow()

    fun isLogin() = checkAuthUseCase.execute()

    fun logIn(login: String, password: String) = viewModelScope.launch {
        val body = repository.login(login, password)
        appAuth.setAuth(body)
    }

    fun register(login: String, password: String, name: String) = viewModelScope.launch {
        val body = withContext(Dispatchers.Default) {
            repository.register(login, password, name, avatar.value)
        }
        appAuth.setAuth(body)
        clearAvatar()
    }

    fun logout() = viewModelScope.launch {
        appAuth.removeAuth()
    }

    fun saveAvatar(uri: Uri?, toFile: File?) = viewModelScope.launch {
        _avatar.value = MediaModel(uri, toFile, Attachment.Type.IMAGE)
    }

    fun clearAvatar() = viewModelScope.launch {
        _avatar.value = null
    }
}