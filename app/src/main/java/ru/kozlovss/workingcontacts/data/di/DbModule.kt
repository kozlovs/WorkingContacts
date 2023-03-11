package ru.kozlovss.workingcontacts.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kozlovss.workingcontacts.data.postsdata.db.PostDb
import ru.kozlovss.workingcontacts.data.eventsdata.db.EventDb
import ru.kozlovss.workingcontacts.data.mywalldata.db.MyWallDb
import ru.kozlovss.workingcontacts.data.walldata.db.UserWallDb
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
    fun providePostDao(db: PostDb) = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: PostDb) = db.postRemoteKeyDao()

    @Singleton
    @Provides
    fun provideEventDb(
        @ApplicationContext
        context: Context
    ): EventDb = Room.databaseBuilder(context, EventDb::class.java, "event.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideEventDao(db: EventDb) = db.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(db: EventDb) = db.eventRemoteKeyDao()

    @Singleton
    @Provides
    fun provideMyWallDb(
        @ApplicationContext
        context: Context
    ): MyWallDb = Room.databaseBuilder(context, MyWallDb::class.java, "my_wall.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideMyWallDao(db: MyWallDb) = db.myWallDao()

    @Provides
    fun provideMyWallRemoteKeyDao(db: MyWallDb) = db.myWallRemoteKeyDao()

    @Singleton
    @Provides
    fun provideUserWallDb(
        @ApplicationContext
        context: Context
    ): UserWallDb = Room.databaseBuilder(context, UserWallDb::class.java, "user_wall.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideUserWallDao(db: UserWallDb) = db.userWallDao()

    @Provides
    fun provideUserWallRemoteKeyDao(db: UserWallDb) = db.userWallRemoteKeyDao()
}