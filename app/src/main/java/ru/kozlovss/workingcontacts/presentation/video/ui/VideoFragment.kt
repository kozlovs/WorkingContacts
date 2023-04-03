package ru.kozlovss.workingcontacts.presentation.video.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.viewModels
import ru.kozlovss.workingcontacts.databinding.FragmentVideoBinding
import ru.kozlovss.workingcontacts.domain.util.StringArg
import ru.kozlovss.workingcontacts.presentation.video.viewmodel.VideoViewModel

class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private val viewModel: VideoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val uri = Uri.parse(arguments?.url!!)
        val mediaController = MediaController(context)
        binding = FragmentVideoBinding.inflate(inflater, container, false)
            .apply {
                video.setVideoURI(uri)
                mediaController.setAnchorView(video)
                mediaController.setMediaPlayer(video)
                video.setMediaController(mediaController)
                video.setOnPreparedListener {

                }
                video.setOnClickListener {
                    (it as VideoView).start()
                }
            }
        return binding.root
    }

    companion object {
        var Bundle.url: String? by StringArg
    }
}