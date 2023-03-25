package ru.kozlovss.workingcontacts.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepository
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepositoryImpl
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepository
import ru.kozlovss.workingcontacts.data.jobsdata.repository.JobRepositoryImpl
import ru.kozlovss.workingcontacts.data.mywalldata.repository.MyWallRepository
import ru.kozlovss.workingcontacts.data.mywalldata.repository.MyWallRepositoryImpl
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepositoryImpl
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepositoryImpl
import ru.kozlovss.workingcontacts.data.walldata.repository.UserWallRepository
import ru.kozlovss.workingcontacts.data.walldata.repository.UserWallRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindsEventRepository(impl: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    fun bindsMyWallRepository(impl: MyWallRepositoryImpl): MyWallRepository

    @Singleton
    @Binds
    fun bindsUserWallRepository(impl: UserWallRepositoryImpl): UserWallRepository

    @Singleton
    @Binds
    fun bindsJobsRepository(impl: JobRepositoryImpl): JobRepository
}