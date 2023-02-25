package ru.kozlovss.workingcontacts.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kozlovss.workingcontacts.data.db.Db
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {
    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): Db = Room.databaseBuilder(context, Db::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(
        db: Db
    ): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(
        db: Db
    ): PostRemoteKeyDao = db.postRemoteKeyDao()
}