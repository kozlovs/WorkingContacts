package ru.kozlovss.workingcontacts.presentation.newjob.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.presentation.newjob.model.NewJobModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NewJobViewModel @Inject constructor(
    private val repository: JobRepository
) : ViewModel() {

    private val _jobData = MutableStateFlow<Job?>(null)
    val jobData = _jobData.asStateFlow()

    private val _state =
        MutableStateFlow<NewJobModel.State>(NewJobModel.State.Idle)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
        val events = _events.asSharedFlow()

    fun getData(id: Long) = viewModelScope.launch {
        _jobData.value = repository.getMyJobs().find { it.id == id }
    }

    fun clearData() {
        _jobData.value = null
        _state.value = NewJobModel.State.Idle
    }

    fun save(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?
    ) = viewModelScope.launch {
        try {
            _state.value = NewJobModel.State.Loading
            val job = Job(
                0,
                name,
                position,
                LocalDate.parse(start, DateTimeFormatter.ISO_DATE).atStartOfDay().toString(),
                finish?.let {
                    LocalDate.parse(finish, DateTimeFormatter.ISO_DATE).atStartOfDay().toString()
                },
                link
            )
            repository.save(job)
            clearData()
            _events.emit(Event.CreateNewItem)
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = NewJobModel.State.Error
        }
    }

    sealed class Event {
        object NavigateToSettings: Event()
        object CreateNewItem: Event()
        data class ShowSnackBar(val text: String): Event()
        data class ShowToast(val text: String): Event()
    }
}