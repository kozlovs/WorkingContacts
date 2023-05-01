package ru.kozlovss.workingcontacts.data.extensions

import com.google.gson.Gson
import retrofit2.Response
import ru.kozlovss.workingcontacts.domain.error.AppError

fun <T> Response<T>.checkAndGetBody(): T {
    if (!this.isSuccessful) {
        val error = Gson().fromJson(this.errorBody()!!.charStream(), AppError.ApiError::class.java)
        throw error
    }
    return this.body() ?: throw AppError.ApiError("empty response body")
}