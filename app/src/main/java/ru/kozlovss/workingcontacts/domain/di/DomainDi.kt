package ru.kozlovss.workingcontacts.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.videoplayer.VideoPlayer
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DomainDi {
    @Provides
    @Singleton
    fun provideAudioPlayer() = AudioPlayer()

    @Provides
    @Singleton
    fun provideVideoPlayer() = VideoPlayer()
}