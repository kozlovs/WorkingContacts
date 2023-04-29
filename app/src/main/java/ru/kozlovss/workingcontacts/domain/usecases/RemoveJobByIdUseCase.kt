package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class RemoveJobByIdUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend fun execute(id: Long) {
        try {
            return jobRepository.removeJobById(id)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}