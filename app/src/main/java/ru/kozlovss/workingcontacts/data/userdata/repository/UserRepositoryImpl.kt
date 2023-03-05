package ru.kozlovss.workingcontacts.data.userdata.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import ru.kozlovss.workingcontacts.data.userdata.api.UserApiService
import ru.kozlovss.workingcontacts.data.userdata.dto.AuthenticationRequest
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.userdata.dto.Token
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val appAuth: AppAuth
) : UserRepository {

    override val myData = MutableStateFlow<User?>(null)

    override suspend fun register(
        login: String,
        password: String,
        name: String,
        photoModel: PhotoModel?
    ): Token {
        val mediaType = "text/plain".toMediaType()
        try {
            val loginRequestBody = login.toRequestBody(mediaType)
            val passwordRequestBody = password.toRequestBody(mediaType)
            val nameRequestBody = name.toRequestBody(mediaType)
            val file = photoModel?.let {
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
            return checkResponse(response)
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
            return checkResponse(response)

        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveTokenOfUser(id: Long, token: String) {
        appAuth.setAuth(id, token)
        myData.value = checkResponse(apiService.getUserById(id))
    }

    override suspend fun clearTokenOfUser() {
        appAuth.removeAuth()
        myData.value = null
    }

    private fun <T> checkResponse(response: Response<T>): T {
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        return response.body() ?: throw RuntimeException("body is null")
    }
}