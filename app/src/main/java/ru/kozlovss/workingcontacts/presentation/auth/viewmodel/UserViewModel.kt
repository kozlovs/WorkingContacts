package ru.kozlovss.workingcontacts.presentation.auth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.userdata.dto.Token
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val appAuth: AppAuth
): ViewModel() {
    val token: StateFlow<Token?> = appAuth.authStateFlow

    private val _avatar = MutableStateFlow<PhotoModel?>(null)
    val avatar: StateFlow<PhotoModel?>
        get() = _avatar

    fun isLogin() = appAuth.isLogin()

    fun logIn(login: String, password: String) {
        viewModelScope.launch {
            val body = repository.login(login, password)
            appAuth.setAuth(body)
        }
    }

    fun register(login: String, password: String, name: String) {
        viewModelScope.launch {
            val body = withContext(Dispatchers.Default) {
                repository.register(login, password, name, avatar.value)
            }
            appAuth.setAuth(body)
            clearAvatar()
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearTokenOfUser()
        }
    }

    fun saveAvatar(uri: Uri?, toFile: File?) {
        _avatar.value = PhotoModel(uri, toFile)
    }

    fun clearAvatar() {
        _avatar.value = null
    }
}