package ru.kozlovss.workingcontacts.data.mywalldata.api

import retrofit2.Response
import retrofit2.http.*
import ru.kozlovss.workingcontacts.entity.Post

interface MyWallApiService {

    @GET("my/wall")
    suspend fun getMyWallPosts(): Response<List<Post>>

    @GET("my/wall/latest")
    suspend fun getMyWallLatestPosts(@Query("count") count: Int): Response<List<Post>>

    @GET("my/wall/{id}/after")
    suspend fun getMyWallPostsAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("my/wall/{id}/before")
    suspend fun getMyWallPostsBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("my/wall/{id}/newer")
    suspend fun getMyWallNewerPosts(@Path("id") id: Long): Response<List<Post>>
}