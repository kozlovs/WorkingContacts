package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.domain.repository.JobRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class RemoveJobByIdUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend fun execute(id: Long) = mapExceptions {
        jobRepository.removeJobById(id)
    }
}