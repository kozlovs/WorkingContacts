package ru.kozlovss.workingcontacts.data.api

import retrofit2.Response
import retrofit2.http.*
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface MyWallApiService {

    @GET("my/wall")
    suspend fun getMyWallPost(): Response<List<Post>>

    @GET("my/wall/latest")
    suspend fun getMyWallLatestPost(@Query("count") count: Int): Response<List<Post>>

    @GET("my/wall/{id}/after")
    suspend fun getMyWallPostsAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("my/wall/{id}/before")
    suspend fun getMyWallPostsBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("my/wall/{id}/newer")
    suspend fun getMyWallNewerPosts(@Path("id") id: Long): Response<List<Post>>
}