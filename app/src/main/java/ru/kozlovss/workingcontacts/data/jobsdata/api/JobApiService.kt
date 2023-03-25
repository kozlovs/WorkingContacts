package ru.kozlovss.workingcontacts.data.jobsdata.api

import retrofit2.Response
import retrofit2.http.*
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job

interface JobApiService {

    @GET("my/jobs")
    suspend fun getMyJobs(): Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveMyJob(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{id}")
    suspend fun deleteMyJobById(@Path("id") id: Long): Response<Unit>

    @GET("{id}/jobs")
    suspend fun getJobsByUserId(@Path("id") id: Long): Response<List<Job>>
}