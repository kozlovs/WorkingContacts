package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.domain.repository.EventRepository
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.domain.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class SwitchAudioUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val eventRepository: EventRepository,
    private val audioPlayer: AudioPlayer
) {
    suspend fun execute(post: Post) = mapExceptions {
        if (post.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(post.attachment)
            postRepository.switchAudioPlayer(post, audioPlayer.isPlaying.value)
        }
    }

    suspend fun execute(event: Event) = mapExceptions {
        if (event.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(event.attachment)
            eventRepository.switchAudioPlayer(event, audioPlayer.isPlaying.value)
        }
    }
}