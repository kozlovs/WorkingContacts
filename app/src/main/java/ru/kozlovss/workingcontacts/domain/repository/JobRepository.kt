package ru.kozlovss.workingcontacts.domain.repository

import ru.kozlovss.workingcontacts.entity.Job

interface JobRepository {
    suspend fun getMyJobs(): List<Job>
    suspend fun getJobsByUserId(id: Long): List<Job>
    suspend fun save(job: Job): Job
    suspend fun removeJobById(id: Long)
}