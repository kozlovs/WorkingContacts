package ru.kozlovss.workingcontacts.presentation.newjob.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.domain.util.SingleLiveEvent
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

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated = _jobCreated as LiveData<Unit>

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
            _jobCreated.value = Unit
            clearData()
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = NewJobModel.State.Error
        }
    }
}