package ru.kozlovss.workingcontacts.presentation.newevent.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepository
import javax.inject.Inject

@HiltViewModel
class NewEventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {


    sealed class Event {
        object NavigateToSettings: Event()
        object CreateNewItem: Event()
        data class ShowSnackBar(val text: String): Event()
        data class ShowToast(val text: String): Event()
    }
}