package ru.kozlovss.workingcontacts.data.walldata.repository

import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.walldata.api.UserWallApiService
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import ru.kozlovss.workingcontacts.domain.util.ResponseChecker
import java.io.IOException
import javax.inject.Inject

class UserWallRepositoryImpl @Inject constructor(
    private val wallApiService: UserWallApiService,
    private val postApiService: PostApiService
) : UserWallRepository {
    override suspend fun getById(id: Long): Post {
        try {
            val response = postApiService.getPostById(id)
            return ResponseChecker.check(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun getAll(id: Long): List<Post> {
        return try {
            val response = wallApiService.getWallByUserId(id)
            return ResponseChecker.check(response)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }
}