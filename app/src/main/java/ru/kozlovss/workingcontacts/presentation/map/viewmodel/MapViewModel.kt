package ru.kozlovss.workingcontacts.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kozlovss.workingcontacts.data.dto.Coordinates

class MapViewModel: ViewModel() {
    private val _coordinates = MutableStateFlow<Coordinates?>(null)
    val coordinates = _coordinates.asStateFlow()

    fun setPlace(coordinates: Coordinates) {
        _coordinates.value = coordinates
    }

    fun clearPlace() {
        _coordinates.value = null
    }
}