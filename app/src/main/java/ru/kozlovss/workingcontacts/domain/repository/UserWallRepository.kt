package ru.kozlovss.workingcontacts.domain.repository

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface UserWallRepository {
    suspend fun getAll(id: Long): List<Post>
}