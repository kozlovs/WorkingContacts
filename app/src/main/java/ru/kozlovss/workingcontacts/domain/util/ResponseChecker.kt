package ru.kozlovss.workingcontacts.domain.util

import retrofit2.Response
import ru.kozlovss.workingcontacts.domain.error.ApiError

object ResponseChecker {
    fun <T> check(response: Response<T>): T {
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        return response.body() ?: throw ApiError(response.code(), response.message())
    }
}