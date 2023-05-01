package ru.kozlovss.workingcontacts.domain.usecases

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.dto.EventRequest
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity
import ru.kozlovss.workingcontacts.domain.repository.EventRepository
import ru.kozlovss.workingcontacts.data.mediadata.dto.Media
import ru.kozlovss.workingcontacts.domain.repository.MediaRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class SaveEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val mediaRepository: MediaRepository,
    private val eventDao: EventDao
) {
    suspend fun execute(event: EventRequest, model: MediaModel?) = mapExceptions {
        val media = model?.let { uploadMedia(it) }
        val eventRequest = media?.let {
            event.copy(
                attachment = Attachment(
                    media.url,
                    model.type
                )
            )
        } ?: event
        val eventResponse = eventRepository.save(eventRequest)
        eventDao.insert(EventEntity.fromDto(eventResponse))
    }

    private suspend fun uploadMedia(mediaModel: MediaModel): Media {
        val media = MultipartBody.Part.createFormData(
            "file",
            mediaModel.file?.name,
            requireNotNull(mediaModel.file?.asRequestBody())
        )
        return mediaRepository.uploadMedia(media)
    }
}