package ru.kozlovss.workingcontacts.domain.repository

import okhttp3.MultipartBody
import ru.kozlovss.workingcontacts.data.mediadata.dto.Media

interface MediaRepository {
    suspend fun uploadMedia(media: MultipartBody.Part) : Media
}