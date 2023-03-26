package ru.kozlovss.workingcontacts.data.jobsdata.repository

import retrofit2.Response
import ru.kozlovss.workingcontacts.data.jobsdata.api.JobApiService
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobApiService: JobApiService
): JobRepository {
    override suspend fun getMyJobs(): List<Job> {
        try {
            val response = jobApiService.getMyJobs()
            return checkResponse(response)
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
            return checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun save(job: Job) {
        try {
            jobApiService.saveMyJob(job)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeJobById(id: Long) {
        try {
            jobApiService.deleteMyJobById(id)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private fun <T> checkResponse(response: Response<T>): T {
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        return response.body() ?: throw ApiError(response.code(), response.message())
    }
}