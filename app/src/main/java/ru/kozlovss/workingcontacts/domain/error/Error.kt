package ru.kozlovss.workingcontacts.domain.error

import kotlinx.coroutines.flow.MutableSharedFlow
import ru.kozlovss.workingcontacts.presentation.util.EventMassage
import java.io.IOException

sealed class AppError : RuntimeException() {
    class ApiError(val reason: String) : AppError()
    class NetworkError : AppError()
    class AuthError : AppError()
    class UnknownError : AppError()
}

sealed class ErrorEvent : EventMassage() {
    class ApiErrorMassage(val message: String?) : ErrorEvent()
    object AuthErrorMassage : ErrorEvent()
    object NetworkErrorMassage : ErrorEvent()
    object UnknownErrorMassage : ErrorEvent()
}

inline fun <T, R> T.mapExceptions(block: (T) -> R): R {
    try {
        return block(this)
    } catch (e: AppError.ApiError) {
        throw e
    } catch (e: AppError.AuthError) {
        throw e
    } catch (e: IOException) {
        throw AppError.NetworkError()
    } catch (e: Exception) {
        e.printStackTrace()
        throw AppError.UnknownError()
    }
}

suspend inline fun <T, R> T.catchExceptions(
    eventsFlow: MutableSharedFlow<EventMassage>,
    block: (T) -> R
) {
    try {
        block(this)
    } catch (e: AppError.ApiError) {
        e.printStackTrace()
        eventsFlow.emit(ErrorEvent.ApiErrorMassage(e.reason))
    } catch (e: AppError.AuthError) {
        e.printStackTrace()
        eventsFlow.emit(ErrorEvent.AuthErrorMassage)
    } catch (e: AppError.NetworkError) {
        e.printStackTrace()
        eventsFlow.emit(ErrorEvent.NetworkErrorMassage)
    } catch (e: UnknownError) {
        e.printStackTrace()
        eventsFlow.emit(ErrorEvent.UnknownErrorMassage)
    } catch (e: Exception) {
        e.printStackTrace()
        eventsFlow.emit(ErrorEvent.UnknownErrorMassage)
    }
}