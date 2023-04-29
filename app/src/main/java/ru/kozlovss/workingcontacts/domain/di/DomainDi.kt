package ru.kozlovss.workingcontacts.domain.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DomainDi {
    @Provides
    @Singleton
    fun provideAudioPlayer() = AudioPlayer()

    @Provides
    @Singleton
    fun provideAppAuth(
        @ApplicationContext
        context: Context
    ) = AppAuth(context)
}