package ru.kozlovss.workingcontacts.presentation.newjob.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NewJobViewModel @Inject constructor(
    private val repository: JobRepository
) : ViewModel() {

    private val _jobData = MutableStateFlow<Job?>(null)
    val jobData = _jobData.asStateFlow()

    fun getData(id: Long) = viewModelScope.launch {
        _jobData.value = repository.getMyJobs().find { it.id == id }
    }

    fun clearData() {
        _jobData.value = null
    }

    fun save(
        name: String,
        position: String,
        start: String,
        finish: String?,
        link: String?
    ) = viewModelScope.launch {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}