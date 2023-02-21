package ru.kozlovss.workingcontacts.data.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.kozlovss.workingcontacts.data.dto.Media

interface MediaApiService {

    @Multipart
    @POST("media")
    suspend fun createMedia(@Part file: MultipartBody.Part): Response<Media>//todo не уверен в правильности
}