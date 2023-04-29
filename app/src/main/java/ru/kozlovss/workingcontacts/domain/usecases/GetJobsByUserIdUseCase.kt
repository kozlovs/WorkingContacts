package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class GetJobsByUserIdUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend fun execute(id: Long): List<Job> {
        try {
            return jobRepository.getJobsByUserId(id)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}