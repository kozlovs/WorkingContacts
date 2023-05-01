package ru.kozlovss.workingcontacts.domain.error

import java.io.IOException

sealed class AppError(var code: String): RuntimeException()
class ApiError(val reason: String): AppError("api_error")
class NetworkError : AppError("error_network")
class AuthError: AppError("error_auth")
class UnknownError: AppError("error_unknown")

inline fun <T, R> T.catchExceptions(block: (T) -> R): R {
    try {
        return block(this)
    } catch (e: ApiError) {
        throw e
    } catch (e: AuthError) {
        throw e
    } catch (e: IOException) {
        throw NetworkError()
    } catch (e: Exception) {
        e.printStackTrace()
        throw UnknownError()
    }
}