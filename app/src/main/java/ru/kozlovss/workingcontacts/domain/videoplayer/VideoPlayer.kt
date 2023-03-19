package ru.kozlovss.workingcontacts.domain.videoplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.kozlovss.workingcontacts.data.dto.Attachment

class VideoPlayer {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean>
        get() = _isPlaying

    private val _video = MutableStateFlow<Attachment?>(null)
    val video: StateFlow<Attachment?>
        get() = _video
    private var mediaPlayer: MediaPlayer? = null

    init {
        clearVideo()
    }

    private fun initializePlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnCompletionListener {
                Log.d("MyLog", "end track")
                playerStop()
            }
        }
    }

    private fun setTrackToPlayer(newVideo: Attachment) {
        _video.value = newVideo
        mediaPlayer?.setDataSource(video.value!!.url)
        mediaPlayer?.prepare()
    }

    fun switch(newVideo: Attachment) {
        if (mediaPlayer == null) initializePlayer()
        if (isVideoSet(newVideo)) {
            if (mediaPlayer?.isPlaying == true) {
                pause()
            } else {
                play()
            }
        } else {
            play(newVideo)
        }
    }

    private fun pause() {
        mediaPlayer?.pause()
        changeState()
    }

    private fun play(newVideo: Attachment? = null) {
        newVideo?.let {
            playerStop()
            initializePlayer()
            setTrackToPlayer(it)
        }
        mediaPlayer?.start()
        changeState()
    }

    private fun playerStop() {
        mediaPlayer?.release()
        mediaPlayer = null
        clearVideo()
        _isPlaying.value = false
        changeState()
    }

    private fun changeState() {
        _isPlaying.value = mediaPlayer?.isPlaying == true
    }

    private fun clearVideo() {
        _video.value = null
    }

    private fun isVideoSet(checkedVideo: Attachment) = video.value?.url == checkedVideo.url
}