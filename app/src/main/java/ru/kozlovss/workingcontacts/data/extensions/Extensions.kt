package ru.kozlovss.workingcontacts.data.extensions

import com.google.gson.Gson
import retrofit2.Response
import ru.kozlovss.workingcontacts.domain.error.ApiError

fun <T> Response<T>.checkAndGetBody(): T {
    if (!this.isSuccessful) {
        val error = Gson().fromJson(this.errorBody()!!.charStream(), ApiError::class.java)
        throw error
    }
    return this.body() ?: throw ApiError("empty response body")
}