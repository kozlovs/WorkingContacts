package ru.kozlovss.workingcontacts.domain.extensions

import retrofit2.Response
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.error.ApiError

fun <T> Response<T>.checkAndGetBody(): T {
    if (!this.isSuccessful) throw ApiError(this.code(), this.message())
    return this.body() ?: throw ApiError(this.code(), this.message())
}

fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity.Companion::fromDto)