package ru.kozlovss.workingcontacts.data.mediadata.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.kozlovss.workingcontacts.data.mediadata.dto.Media

interface MediaApiService {

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part file: MultipartBody.Part): Response<Media>
}