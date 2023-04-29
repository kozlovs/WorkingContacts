package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class GetMyJobsUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend fun execute(): List<Job> {
        try {
            return jobRepository.getMyJobs()
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}