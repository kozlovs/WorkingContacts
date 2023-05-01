package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class GetJobsByUserIdUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend fun execute(id: Long): List<Job> = mapExceptions {
        return jobRepository.getJobsByUserId(id)
    }
}