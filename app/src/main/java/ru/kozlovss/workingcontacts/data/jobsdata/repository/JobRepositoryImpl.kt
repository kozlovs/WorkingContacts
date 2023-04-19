package ru.kozlovss.workingcontacts.data.jobsdata.repository

import ru.kozlovss.workingcontacts.data.jobsdata.api.JobApiService
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import ru.kozlovss.workingcontacts.domain.util.ResponseChecker
import java.io.IOException
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobApiService: JobApiService
): JobRepository {
    override suspend fun getMyJobs(): List<Job> {
        try {
            val response = jobApiService.getMyJobs()
            return ResponseChecker.check(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }

    override suspend fun getJobsByUserId(id: Long): List<Job> {
        try {
            val response = jobApiService.getJobsByUserId(id)
            return ResponseChecker.check(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }

    override suspend fun save(job: Job) {
        try {
            jobApiService.saveMyJob(job)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }

    override suspend fun removeJobById(id: Long) {
        try {
            jobApiService.deleteMyJobById(id)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}