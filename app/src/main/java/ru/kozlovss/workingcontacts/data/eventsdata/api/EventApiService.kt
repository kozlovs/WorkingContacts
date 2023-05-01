package ru.kozlovss.workingcontacts.data.eventsdata.api

import retrofit2.Response
import retrofit2.http.*
import ru.kozlovss.workingcontacts.entity.Event
import ru.kozlovss.workingcontacts.entity.EventRequest

interface EventApiService {

    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>

    @POST("events")
    suspend fun saveEvent(@Body event: EventRequest): Response<Event>

    @GET("events/latest")
    suspend fun getLatestEvents(@Query("count") count: Int): Response<List<Event>>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}")
    suspend fun deleteEventById(@Path("id") id: Long): Response<Unit>

    @GET("events/{id}/after")
    suspend fun getEventsAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Event>>

    @GET("events/{id}/before")
    suspend fun getEventsBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Event>>

    @POST("events/{id}/likes")
    suspend fun likeEventById(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeEventById(@Path("id") id: Long): Response<Event>

    @GET("events/{id}/newer")
    suspend fun getNewerEvents(@Path("id") id: Long): Response<List<Event>>

    @POST("events/{id}/participants")
    suspend fun participateEventById(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/participants")
    suspend fun notParticipateEventById(@Path("id") id: Long): Response<Event>
}