package ru.kozlovss.workingcontacts.data.eventsdata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.kozlovss.workingcontacts.data.mediadata.api.MediaApiService
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.mediadata.dto.Media
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.eventsdata.api.EventApiService
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventRemoteKeyDao
import ru.kozlovss.workingcontacts.data.eventsdata.db.EventDb
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.dto.EventRequest
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import ru.kozlovss.workingcontacts.domain.util.ResponseChecker
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
        val response = apiService.getEventById(id)
        return ResponseChecker.check(response)
    }

    override suspend fun likeById(id: Long): Event {
        val response = apiService.likeEventById(id)
        return ResponseChecker.check(response)
    }

    override suspend fun dislikeById(id: Long): Event {
        val response = apiService.dislikeEventById(id)
        return ResponseChecker.check(response)
    }

    override suspend fun participateById(id: Long): Event {
        val response = apiService.participateEventById(id)
        return ResponseChecker.check(response)

    }

    override suspend fun notParticipateById(id: Long): Event {
        val response = apiService.notParticipateEventById(id)
        return ResponseChecker.check(response)
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = apiService.deleteEventById(id)
            ResponseChecker.check(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }

    override suspend fun save(event: EventRequest, model: MediaModel?) {
        try {
            val media = model?.let { upload(it) }
            val response = media?.let {
                apiService.saveEvent(
                    event.copy(
                        attachment = Attachment(
                            media.url,
                            model.type
                        )
                    )
                )
            } ?: apiService.saveEvent(event)
            val body = ResponseChecker.check(response)
            dao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun switchAudioPlayer(event: Event, audioPlayerState: Boolean) {
        val newEvent = event.copy(
            isPaying = audioPlayerState
        )
        dao.update(EventEntity.fromDto(newEvent))
    }

    private suspend fun upload(mediaModel: MediaModel): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file",
                mediaModel.file?.name,
                requireNotNull(mediaModel.file?.asRequestBody())
            )
            val response = mediaApiService.uploadMedia(media)
            return ResponseChecker.check(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}