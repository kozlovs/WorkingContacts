package ru.kozlovss.workingcontacts.domain.error

import java.io.IOException
import java.lang.UnknownError

sealed class AppError(var code: String): RuntimeException()
class ApiError(val status: Int, code: String): AppError(code)
class NetworkError : AppError("error_network")
class AuthError: AppError("error_auth")
class UnknownError: AppError("error_unknown")

inline fun <T, R> T.catchExceptions(block: (T) -> R): R {
    try {
        return block(this)
    } catch (e: IOException) {
        throw NetworkError()
    } catch (e: Exception) {
        e.printStackTrace()
        throw UnknownError()
    }
}