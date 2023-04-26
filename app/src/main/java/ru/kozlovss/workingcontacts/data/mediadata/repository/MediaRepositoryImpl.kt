package ru.kozlovss.workingcontacts.data.mediadata.repository

import okhttp3.MultipartBody
import ru.kozlovss.workingcontacts.data.mediadata.api.MediaApiService
import ru.kozlovss.workingcontacts.data.mediadata.dto.Media
import ru.kozlovss.workingcontacts.domain.util.ResponseChecker
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val apiService: MediaApiService
): MediaRepository {
    override suspend fun uploadMedia(media: MultipartBody.Part): Media {
        val response = apiService.uploadMedia(media)
        return ResponseChecker.check(response)
    }
}