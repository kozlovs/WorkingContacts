package ru.kozlovss.workingcontacts.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.kozlovss.workingcontacts.data.dto.AuthenticationRequest
import ru.kozlovss.workingcontacts.data.dto.Token
import ru.kozlovss.workingcontacts.data.dto.User

interface UserApiService {

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>

    @POST("users/authentication")
    suspend fun logIn(@Body authenticationRequest: AuthenticationRequest): Response<Token>

    @Multipart
    @POST("users/registration")
    suspend fun signIn(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part?
    ): Response<Token>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>
}