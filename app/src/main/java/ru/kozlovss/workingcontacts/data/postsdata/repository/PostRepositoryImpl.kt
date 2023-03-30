package ru.kozlovss.workingcontacts.data.postsdata.repository

import android.util.Log
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.kozlovss.workingcontacts.data.mediadata.api.MediaApiService
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.db.PostDb
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.mediadata.dto.Media
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import ru.kozlovss.workingcontacts.data.postsdata.dto.PostRequest
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val myWallDao: MyWallDao,
    private val apiService: PostApiService,
    private val mediaApiService: MediaApiService,
    remoteKeyDao: PostRemoteKeyDao,
    db: PostDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = PostRemoteMediator(
            apiService,
            dao,
            remoteKeyDao,
            db
        )
    ).flow.map { it.map(PostEntity::toDto) }

    override suspend fun getById(id: Long): Post {
        try {
            val response = apiService.getPostById(id)
            return checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
//        } catch (e: Exception) {
//            throw UnknownError()
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
            val response = apiService.likePostById(id)
            val body = checkResponse(response)
            val post = PostEntity.fromDto(body)
            dao.insert(post)
            if (post.ownedByMe) myWallDao.insert(post)
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
            val post = PostEntity.fromDto(body)
            dao.insert(post)
            if (post.ownedByMe) myWallDao.insert(post)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            myWallDao.removeById(id)
            val response = apiService.deletePostById(id)
            checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun save(post: PostRequest, model: MediaModel?) {
        try {
            val media = model?.let { upload(it) }
            val response = media?.let {
                apiService.savePost(
                    post.copy(
                        attachment = Attachment(
                            media.url,
                            model.type
                        )
                    )
                ) } ?: apiService.savePost(post)
            Log.d("MyLog", "repository response ${response.isSuccessful}")
            val body = checkResponse(response)
            Log.d("MyLog", "repository post body $body")
            dao.save(PostEntity.fromDto(body))
            myWallDao.save(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun switchAudioPlayer(post: Post, audioPlayerState: Boolean) {
        val newPost = post.copy(
            isPaying = audioPlayerState
        )
        Log.d("MyLog", "audioPlayerState: $audioPlayerState")
        Log.d("MyLog", "post: ${post.attachment}")
        Log.d("MyLog", "newPost: ${newPost.attachment}")
        dao.update(PostEntity.fromDto(newPost))
    }

    override suspend fun stopAudioPlayer() {
        dao.stopPlayer()
    }

    private suspend fun upload(mediaModel: MediaModel): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file",
                mediaModel.file?.name,
                requireNotNull(mediaModel.file?.asRequestBody())
            )
            val response = mediaApiService.createMedia(media)
            return checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }

    private fun <T> checkResponse(response: Response<T>): T {
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        return response.body() ?: throw ApiError(response.code(), response.message())
    }
}