package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.audioplayer.AudioPlayer
import javax.inject.Inject

class SwitchAudioUseCase @Inject constructor(
    private val repository: PostRepository,
    private val audioPlayer: AudioPlayer
) {
    suspend fun execute(post: Post) {
        if (post.attachment?.type == Attachment.Type.AUDIO) {
            audioPlayer.switch(post.attachment)
            repository.switchAudioPlayer(post, audioPlayer.isPlaying.value)
        }
    }
}