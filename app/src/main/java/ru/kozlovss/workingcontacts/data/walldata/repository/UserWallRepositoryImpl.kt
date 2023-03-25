package ru.kozlovss.workingcontacts.data.walldata.repository

import retrofit2.Response
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.walldata.api.UserWallApiService
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class UserWallRepositoryImpl @Inject constructor(
    private val wallApiService: UserWallApiService,
    private val postApiService: PostApiService,
    private val dao: PostDao
) : UserWallRepository {
    override suspend fun getById(id: Long): Post {
        try {
            val response = postApiService.getPostById(id)
            return checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun getAll(id: Long): List<Post> {
        return try {
            val response = wallApiService.getWallByUserId(id)
            return checkResponse(response)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun likeById(id: Long) {
        val post = getById(id)
        if (post.likedByMe) {
            makeRequestDislikeById(id)
        } else {
            makeRequestLikeById(id)
        }
    }

    private suspend fun makeRequestLikeById(id: Long) {
        try {
            val response = postApiService.likePostById(id)
            val body = checkResponse(response)
            if (dao.containsPostWithId(id)) dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private suspend fun makeRequestDislikeById(id: Long) {
        try {
            val response = postApiService.dislikePostById(id)
            val body = checkResponse(response)
            if (dao.containsPostWithId(id)) dao.insert(PostEntity.fromDto(body))
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