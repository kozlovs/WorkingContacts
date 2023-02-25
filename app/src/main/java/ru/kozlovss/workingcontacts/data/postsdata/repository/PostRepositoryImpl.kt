package ru.kozlovss.workingcontacts.data.postsdata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.kozlovss.workingcontacts.BuildConfig.BASE_URL
import ru.kozlovss.workingcontacts.data.api.MediaApiService
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.db.Db
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Media
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: PostApiService,
    private val mediaApiService: MediaApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    db: Db
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = postDao::pagingSource,
        remoteMediator = PostRemoteMediator(
            apiService,
            postDao,
            postRemoteKeyDao,
            db
        )
    ).flow.map { it.map(PostEntity::toDto) }

    override suspend fun getById(id: Long): Post {
        try {
            val response = apiService.getPostById(id)
            return checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeById(id: Long) {
        val post = getById(id)
        postDao.likeById(id)
        if (post.likedByMe) {
            makeRequestDislikeById(id)
        } else {
            makeRequestLikeById(id)
        }
    }

    private suspend fun makeRequestLikeById(id: Long) {
        try {
            val response = apiService.likePostById(id)
            val body = checkResponse(response)
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private suspend fun makeRequestDislikeById(id: Long) {
        try {
            val response = apiService.dislikePostById(id)
            val body = checkResponse(response)
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            postDao.removeById(id)
            val response = apiService.deletePostById(id)
            checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun save(post: Post) {
        try {
            val newPostId = postDao.insert(PostEntity.fromDto(post))
            val response = apiService.savePost(post.toRequest())
            val body = checkResponse(response)
            postDao.removeById(newPostId)
            postDao.save(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveWithAttachment(post: Post, photo: PhotoModel) {
        try {
            val media = upload(photo)
            val newPostId = postDao.insert(PostEntity.fromDto(post))
            val response = apiService.savePost(
                post.copy(
                    attachment = Attachment(
                        media.url,
                        Attachment.AttachmentType.IMAGE
                    )
                ).toRequest()
            )
            val body = checkResponse(response)
            postDao.removeById(newPostId)
            postDao.save(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private suspend fun upload(photo: PhotoModel): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file",
                photo.file?.name,
                requireNotNull(photo.file?.asRequestBody())
            )

            val response = mediaApiService.createMedia(media)
            return checkResponse(response)
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

    companion object {
        fun getAvatarUrl(avatarURL: String) = "${BASE_URL}/avatars/$avatarURL"
        fun getImageUrl(imageURL: String) = "${BASE_URL}/media/$imageURL"
    }
}