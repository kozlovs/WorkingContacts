package ru.kozlovss.workingcontacts.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventRemoteKeyDao
import ru.kozlovss.workingcontacts.data.postsdata.db.PostDb
import ru.kozlovss.workingcontacts.data.eventsdata.db.EventDb
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallRemoteKeyDao
import ru.kozlovss.workingcontacts.data.mywalldata.db.MyWallDb
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {
    @Singleton
    @Provides
    fun providePostDb(
        @ApplicationContext
        context: Context
    ): PostDb = Room.databaseBuilder(context, PostDb::class.java, "post.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(
        postDb: PostDb
    ): PostDao = postDb.postDao()

    @Provides
    fun providePostRemoteKeyDao(
        postDb: PostDb
    ): PostRemoteKeyDao = postDb.postRemoteKeyDao()

    @Singleton
    @Provides
    fun provideEventDb(
        @ApplicationContext
        context: Context
    ): EventDb = Room.databaseBuilder(context, EventDb::class.java, "event.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideEventDao(
        eventDb: EventDb
    ): EventDao = eventDb.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(
        eventDb: EventDb
    ): EventRemoteKeyDao = eventDb.eventRemoteKeyDao()

    @Singleton
    @Provides
    fun provideMyWallDb(
        @ApplicationContext
        context: Context
    ): MyWallDb = Room.databaseBuilder(context, MyWallDb::class.java, "my_wall.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideMyWallDao(
        myWallDb: MyWallDb
    ): MyWallDao = myWallDb.myWallDao()

    @Provides
    fun provideMyWallRemoteKeyDao(
        myWallDb: MyWallDb
    ): MyWallRemoteKeyDao = myWallDb.myWallRemoteKeyDao()
}