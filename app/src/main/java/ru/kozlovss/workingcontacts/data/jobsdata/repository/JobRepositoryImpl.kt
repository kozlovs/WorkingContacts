package ru.kozlovss.workingcontacts.data.jobsdata.repository

import ru.kozlovss.workingcontacts.data.extensions.checkAndGetBody
import ru.kozlovss.workingcontacts.data.jobsdata.api.JobApiService
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobApiService: JobApiService
) : JobRepository {
    override suspend fun getMyJobs() = jobApiService.getMyJobs().checkAndGetBody()
    override suspend fun getJobsByUserId(id: Long) = jobApiService.getJobsByUserId(id).checkAndGetBody()
    override suspend fun save(job: Job) = jobApiService.saveMyJob(job).checkAndGetBody()
    override suspend fun removeJobById(id: Long) = jobApiService.deleteMyJobById(id).checkAndGetBody()
}