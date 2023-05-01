package ru.kozlovss.workingcontacts.presentation.video.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.databinding.FragmentVideoBinding
import ru.kozlovss.workingcontacts.presentation.util.StringArg
import ru.kozlovss.workingcontacts.presentation.video.model.VideoModel
import ru.kozlovss.workingcontacts.presentation.video.viewmodel.VideoViewModel

class VideoFragment : Fragment() {

    private var binding: FragmentVideoBinding? = null
    private val viewModel: VideoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setStateLoading()

        binding = FragmentVideoBinding.inflate(inflater, container, false)
        setVideo()
        subscribe()

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun subscribe() = with(binding!!) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    progress.isVisible = state is VideoModel.State.Loading
                    errorLayout.isVisible = state is VideoModel.State.Error
                    if (state is VideoModel.State.Idle) videoLayout.visibility = View.VISIBLE
                    else videoLayout.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setVideo() = with(binding!!.video) {
        val uri = Uri.parse(arguments?.url!!)
        val mediaController = MediaController(context)
        setOnErrorListener { _, _, _ ->
            viewModel.setStateError()
            true
        }
        setVideoURI(uri)
        mediaController.setAnchorView(this)
        mediaController.setMediaPlayer(this)
        setMediaController(mediaController)
        setOnPreparedListener {
        viewModel.setStateIde()
            start()
        }
    }

    companion object {
        var Bundle.url: String? by StringArg
    }
}