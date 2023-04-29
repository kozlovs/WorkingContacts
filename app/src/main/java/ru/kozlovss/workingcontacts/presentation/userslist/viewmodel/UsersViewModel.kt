package ru.kozlovss.workingcontacts.presentation.userslist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.domain.usecases.GetUsersUseCase
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _usersData = MutableStateFlow(emptyList<User>())
    val userData = _usersData.asStateFlow()

    fun getData() = viewModelScope.launch {
        _usersData.value = getUsersUseCase.execute()
    }

    fun clearData() = viewModelScope.launch {
        _usersData.value = emptyList()
    }
}