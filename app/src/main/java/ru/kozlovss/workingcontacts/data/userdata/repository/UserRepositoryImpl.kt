package ru.kozlovss.workingcontacts.data.userdata.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.kozlovss.workingcontacts.data.userdata.api.UserApiService
import ru.kozlovss.workingcontacts.data.userdata.dto.AuthenticationRequest
import ru.kozlovss.workingcontacts.data.userdata.dto.Token
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.extensions.checkAndGetBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val appAuth: AppAuth
) : UserRepository {

    override suspend fun register(
        login: RequestBody,
        password: RequestBody,
        name: RequestBody,
        avatar: MultipartBody.Part?
    ) = apiService.signIn(
        login,
        password,
        name,
        avatar
    ).checkAndGetBody()

    override suspend fun login(login: String, password: String) =
        apiService
            .logIn(AuthenticationRequest(login, password))
            .checkAndGetBody()

    override suspend fun saveTokenOfUser(token: Token) = appAuth.setAuth(token)
    override suspend fun clearTokenOfUser() = appAuth.removeAuth()
    override suspend fun getUserById(id: Long) = apiService.getUserById(id).checkAndGetBody()
    override suspend fun getUsers() = apiService.getAllUsers().checkAndGetBody()
}