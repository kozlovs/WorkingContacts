package ru.kozlovss.workingcontacts.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kozlovss.workingcontacts.data.repository.userrepository.UserRepository
import ru.kozlovss.workingcontacts.data.repository.userrepository.UserRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
}