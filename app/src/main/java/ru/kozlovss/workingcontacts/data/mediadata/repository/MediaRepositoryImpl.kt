package ru.kozlovss.workingcontacts.data.mediadata.repository

import okhttp3.MultipartBody
import ru.kozlovss.workingcontacts.data.extensions.checkAndGetBody
import ru.kozlovss.workingcontacts.data.mediadata.api.MediaApiService
import ru.kozlovss.workingcontacts.domain.repository.MediaRepository
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val apiService: MediaApiService
): MediaRepository {
    override suspend fun uploadMedia(media: MultipartBody.Part) = apiService.uploadMedia(media).checkAndGetBody()
}