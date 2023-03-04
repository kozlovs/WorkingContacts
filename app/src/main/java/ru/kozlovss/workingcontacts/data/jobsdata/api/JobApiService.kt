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

    @GET("events/{id}")
    suspend fun getUserJobsById(@Path("id") id: Long): Response<Job>
}