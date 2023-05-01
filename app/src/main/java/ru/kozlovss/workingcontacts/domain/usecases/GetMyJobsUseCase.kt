package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class GetMyJobsUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend fun execute(): List<Job> = mapExceptions {
        return jobRepository.getMyJobs()
    }
}