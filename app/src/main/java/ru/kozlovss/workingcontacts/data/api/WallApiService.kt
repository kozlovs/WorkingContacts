package ru.kozlovss.workingcontacts.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.kozlovss.workingcontacts.data.dto.Post

interface WallApiService {

    @GET("{id}/wall")
    suspend fun getWallByUserId(@Path("id") id: Long): Response<List<Post>>

    @GET("{id}/wall/latest")
    suspend fun getWallLatestByUserId(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("{user_id}/wall/{post_id}/after")
    suspend fun getWallPostsAfterByUserId(@Path("user_id") userId: Long, @Path("post_id") postId: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("{user_id}/wall/{post_id}/before")
    suspend fun getWallPostsBeforeByUserId(@Path("user_id") userId: Long, @Path("post_id") postId: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("{user_id}/wall/{post_id}/newer")
    suspend fun getWallNewerPostsByUserId(@Path("user_id") userId: Long, @Path("post_id") postId: Long): Response<List<Post>>
}