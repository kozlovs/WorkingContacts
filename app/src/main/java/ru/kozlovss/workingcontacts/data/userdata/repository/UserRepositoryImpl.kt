package ru.kozlovss.workingcontacts.data.userdata.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.kozlovss.workingcontacts.data.userdata.api.UserApiService
import ru.kozlovss.workingcontacts.data.userdata.dto.AuthenticationRequest
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.userdata.dto.Token
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import ru.kozlovss.workingcontacts.domain.util.ResponseChecker
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val appAuth: AppAuth
) : UserRepository {

    override suspend fun register(
        login: String,
        password: String,
        name: String,
        mediaModel: MediaModel?
    ): Token {
        val mediaType = "text/plain".toMediaType()
        try {
            val loginRequestBody = login.toRequestBody(mediaType)
            val passwordRequestBody = password.toRequestBody(mediaType)
            val nameRequestBody = name.toRequestBody(mediaType)
            val file = mediaModel?.let {
                MultipartBody.Part.createFormData(
                    "file",
                    it.file?.name,
                    requireNotNull(it.file?.asRequestBody())
                )
            }

            val response = apiService.signIn(
                loginRequestBody,
                passwordRequestBody,
                nameRequestBody,
                file
            )
            return ResponseChecker.check(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun login(login: String, password: String): Token {
        try {
            val response = withContext(Dispatchers.Default) {
                apiService.logIn(AuthenticationRequest(login, password))
            }
            return ResponseChecker.check(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveTokenOfUser(token: Token) {
        appAuth.setAuth(token)
    }

    override suspend fun clearTokenOfUser() {
        appAuth.removeAuth()
    }

    override suspend fun getMyData(id: Long) = ResponseChecker.check(apiService.getUserById(id))

    override suspend fun getUserById(id: Long) = ResponseChecker.check(apiService.getUserById(id))

    override suspend fun getUsers() = ResponseChecker.check(apiService.getAllUsers())
}