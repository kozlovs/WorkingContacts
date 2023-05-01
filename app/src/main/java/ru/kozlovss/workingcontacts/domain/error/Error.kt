package ru.kozlovss.workingcontacts.domain.error

import java.io.IOException

sealed class AppError: RuntimeException()
class ApiError(val reason: String): AppError()
class NetworkError : AppError()
class AuthError: AppError()
class UnknownError: AppError()

inline fun <T, R> T.mapExceptions(block: (T) -> R): R {
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