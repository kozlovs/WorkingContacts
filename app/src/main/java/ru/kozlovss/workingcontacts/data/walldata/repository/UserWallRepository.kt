package ru.kozlovss.workingcontacts.data.walldata.repository

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface UserWallRepository {
    suspend fun getById(id: Long): Post?
    suspend fun getAll(id: Long): List<Post>
}