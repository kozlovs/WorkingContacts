package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class CheckAuthUseCase @Inject constructor(
    private val appAuth: AppAuth
) {
    fun execute(): Boolean {
        try {
            return appAuth.isAuthenticated()
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}