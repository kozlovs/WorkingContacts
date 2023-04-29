package ru.kozlovss.workingcontacts.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.Coordinates

class MapViewModel: ViewModel() {
    private val _coordinates = MutableStateFlow<Coordinates?>(null)
    val coordinates = _coordinates.asStateFlow()

    fun setPlace(coordinates: Coordinates) = viewModelScope.launch {
        _coordinates.value = coordinates
    }

    fun clearPlace() = viewModelScope.launch {
        _coordinates.value = null
    }
}