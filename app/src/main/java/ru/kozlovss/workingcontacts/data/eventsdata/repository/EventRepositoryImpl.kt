package ru.kozlovss.workingcontacts.data.eventsdata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.kozlovss.workingcontacts.BuildConfig
import ru.kozlovss.workingcontacts.data.api.MediaApiService
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Media
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.eventsdata.api.EventApiService
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventRemoteKeyDao
import ru.kozlovss.workingcontacts.data.eventsdata.db.EventDb
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val dao: EventDao,
    private val apiService: EventApiService,
    private val mediaApiService: MediaApiService,
    remoteKeyDao: EventRemoteKeyDao,
    db: EventDb
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val events: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = EventRemoteMediator(
            apiService,
            dao,
            remoteKeyDao,
            db
        )
    ).flow.map { it.map(EventEntity::toDto) }

    override suspend fun getById(id: Long): Event {
        try {
            val response = apiService.getEventById(id)
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
            val response = apiService.likeEventById(id)
            val body = checkResponse(response)
            dao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private suspend fun makeRequestDislikeById(id: Long) {
        try {
            val response = apiService.dislikeEventById(id)
            val body = checkResponse(response)
            dao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = apiService.deleteEventById(id)
            checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun save(post: Event) {
        try {
            val newPostId = dao.insert(EventEntity.fromDto(post))
            val response = apiService.saveEvent(post.toRequest())
            val body = checkResponse(response)
            dao.removeById(newPostId)
            dao.save(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveWithAttachment(post: Event, photo: PhotoModel) {
        try {
            val media = upload(photo)
            val newPostId = dao.insert(EventEntity.fromDto(post))
            val response = apiService.saveEvent(
                post.copy(
                    attachment = Attachment(
                        media.url,
                        Attachment.AttachmentType.IMAGE
                    )
                ).toRequest()
            )
            val body = checkResponse(response)
            dao.removeById(newPostId)
            dao.save(EventEntity.fromDto(body))
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
        fun getImageUrl(imageURL: String) = "${BuildConfig.BASE_URL}/media/$imageURL"
    }
}