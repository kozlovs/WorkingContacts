package ru.kozlovss.workingcontacts.data.mywalldata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.mediadata.api.MediaApiService
import ru.kozlovss.workingcontacts.data.mediadata.dto.Media
import ru.kozlovss.workingcontacts.data.mywalldata.api.MyWallApiService
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallRemoteKeyDao
import ru.kozlovss.workingcontacts.data.mywalldata.db.MyWallDb
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class MyWallRepositoryImpl @Inject constructor(
    private val dao: MyWallDao,
    wallApiService: MyWallApiService,
    private val postApiService: PostApiService,
    private val mediaApiService: MediaApiService,
    remoteKeyDao: MyWallRemoteKeyDao,
    db: MyWallDb
) : MyWallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = MyWallRemoteMediator(
            wallApiService,
            dao,
            remoteKeyDao,
            db
        )
    ).flow.map { it.map(PostEntity::toDto) }

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

    override suspend fun likeById(id: Long) {
        val post = getById(id)
        dao.likeById(id)
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
            dao.insert(PostEntity.fromDto(body))
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
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = postApiService.deletePostById(id)
            checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun save(post: Post) {
        try {
            val newPostId = dao.insert(PostEntity.fromDto(post))
            val response = postApiService.savePost(post.toRequest())
            val body = checkResponse(response)
            dao.removeById(newPostId)
            dao.save(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveWithAttachment(post: Post, photo: PhotoModel) {
        try {
            val media = upload(photo)
            val newPostId = dao.insert(PostEntity.fromDto(post))
            val response = postApiService.savePost(
                post.copy(
                    attachment = Attachment(
                        media.url,
                        Attachment.AttachmentType.IMAGE
                    )
                ).toRequest()
            )
            val body = checkResponse(response)
            dao.removeById(newPostId)
            dao.save(PostEntity.fromDto(body))
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
}