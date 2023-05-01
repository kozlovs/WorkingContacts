package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class SaveJobUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend fun execute(job: Job) = mapExceptions {
        jobRepository.save(job)
    }
}