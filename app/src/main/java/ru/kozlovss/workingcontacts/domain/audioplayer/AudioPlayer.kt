package ru.kozlovss.workingcontacts.domain.audioplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.kozlovss.workingcontacts.data.dto.Attachment

class AudioPlayer {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean>
        get() = _isPlaying

    private val track = MutableStateFlow<Attachment?>(null)
    private var mediaPlayer: MediaPlayer? = null

    init {
        clearTrack()
    }

    fun switch(newTrack: Attachment) {
        if (mediaPlayer == null) initializePlayer()
        if (isTrackSet(newTrack)) {
            if (mediaPlayer?.isPlaying == true) {
                pause()
            } else {
                play()
            }
        } else {
            play(newTrack)
        }
    }

    private fun pause() {
        mediaPlayer?.pause()
        changeState()
    }

    private fun play(newTrack: Attachment? = null) {
        newTrack?.let {
            playerStop()
            initializePlayer()
            setTrackToPlayer(it)
        }
        mediaPlayer?.start()
        changeState()
    }

    private fun changeState() {
        _isPlaying.value = mediaPlayer?.isPlaying == true
    }

    private fun initializePlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnCompletionListener {
                playerStop()
            }
        }
    }

    private fun setTrackToPlayer(newTrack: Attachment) {
        track.value = newTrack
        mediaPlayer?.setDataSource(track.value!!.url)
        mediaPlayer?.prepare()
    }

    private fun clearTrack() {
        track.value = null
    }

    private fun isTrackSet(checkedTrack: Attachment) = track.value?.url == checkedTrack.url

    private fun playerStop() {
        mediaPlayer?.release()
        mediaPlayer = null
        clearTrack()
    }
}