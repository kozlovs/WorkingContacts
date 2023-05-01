package ru.kozlovss.workingcontacts.domain.repository

import ru.kozlovss.workingcontacts.entity.Post

interface UserWallRepository {
    suspend fun getAll(id: Long): List<Post>
}