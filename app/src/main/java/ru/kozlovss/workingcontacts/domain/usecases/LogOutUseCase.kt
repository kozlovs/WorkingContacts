package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val appAuth: AppAuth
) {
    fun execute() {
        try {
            appAuth.removeAuth()
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}