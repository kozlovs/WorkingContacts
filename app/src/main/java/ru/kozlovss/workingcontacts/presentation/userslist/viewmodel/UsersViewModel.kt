package ru.kozlovss.workingcontacts.presentation.userslist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _usersData = MutableStateFlow(emptyList<User>())
    val userData = _usersData.asStateFlow()

    fun getData() = viewModelScope.launch {
        _usersData.value = userRepository.getUsers()
    }

    fun clearData() {
        _usersData.value = emptyList()
    }
}