package ru.kozlovss.workingcontacts.presentation.newpost.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.databinding.FragmentNewPostBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.map.viewmodel.MapViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.model.NewPostModel
import ru.kozlovss.workingcontacts.presentation.newpost.viewmodel.NewPostViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.viewmodel.NewPostViewModel.Event.*


@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val viewModel: NewPostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels()
    private lateinit var binding: FragmentNewPostBinding
    private var postId: Long? = null

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("MyLog", "Selected URI: $uri")
        } else {
            Log.d("MyLog", "No media selected")
        }
    }
//    private val imageLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            when (it.resultCode) {
//                ImagePicker.RESULT_ERROR -> {
//                    Toast.makeText(
//                        requireContext(),
//                        getString(R.string.photo_error),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                Activity.RESULT_OK -> {
//                    val uri = it.data?.data
//                    viewModel.savePhoto(uri, uri?.toFile())
//                }
//            }
//        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        postId = arguments?.postId
        postId?.let { viewModel.getData(it) }
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        subscribe()
        addBackPressedAction()
        setListeners()
        binding.content.requestFocus()

        return binding.root
    }

    private fun addBackPressedAction() {
        val callbackNoEdit = object : OnBackPressedCallback(viewModel.postId.value != null) {
            override fun handleOnBackPressed() {
                viewModel.clearData()
                Log.d("MyLog", "clear data")
                findNavController().navigateUp()
            }
        }
        val callbackWithDraft = object : OnBackPressedCallback(viewModel.postId.value == null) {
            override fun handleOnBackPressed() {
                val content = binding.contentField.text?.trim().toString()
                val link = binding.linkField.text?.trim().toString()
                if (content.isNotBlank()) {
                    viewModel.makeDraft(
                        content,
                        getCoordsData(),
                        link.ifBlank { null }
                    )
                } else viewModel.clearData()
                findNavController().navigateUp()
            }
        }
        val backPressedDispatcher = requireActivity().onBackPressedDispatcher
        backPressedDispatcher.addCallback(viewLifecycleOwner, callbackNoEdit)
        backPressedDispatcher.addCallback(viewLifecycleOwner, callbackWithDraft)
    }

    private fun subscribe() = with(binding) {
        lifecycleScope.launchWhenCreated {
            viewModel.content.collect {
                it?.let { contentField.setText(it) }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.link.collect {
                it?.let { linkField.setText(it) }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.coordinates.collect {
                it?.let {
                    latField.setText(it.lat)
                    lonField.setText(it.longitude)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.attachment.collect {
                attachmentGroup.isVisible = it != null
                it?.let {
//                    when (it.type) {
//                        MediaModel.Type.PHOTO -> {
////                            Glide.with(preview)
////                                .load(it.url)
////                                .placeholder(R.drawable.baseline_update_24)
////                                .error(R.drawable.baseline_error_outline_24)
////                                .timeout(10_000)
////                                .into(preview)
//                        }

//                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.state.collect { state ->
                cardPost.isVisible = state is NewPostModel.State.Idle
                bottomAppBar.isVisible = state is NewPostModel.State.Idle
                save.isVisible = state is NewPostModel.State.Idle
                progress.isVisible = state is NewPostModel.State.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.events.collect {
                when (it) {
                    CreateNewItem -> findNavController().navigateUp()
                    is ShowSnackBar -> Snackbar.make(binding.root, it.text, Snackbar.LENGTH_LONG)
                        .show()
                    is ShowToast -> Toast.makeText(context, it.text, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setListeners() = with(binding) {
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.take_photo -> {
                    //            ImagePicker.Builder(this@NewPostFragment)
//                .cameraOnly()
//                .maxResultSize(2048, 2048)
//                .createIntent(imageLauncher::launch)
                    true
                }
                R.id.add_photo -> {
                    if (isPhotoPickerAvailable()) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
//            ImagePicker.Builder(this@NewPostFragment)
//                .galleryOnly()
//                .maxResultSize(2048, 2048)
//                .createIntent(imageLauncher::launch)
                    true
                }
                R.id.add_video -> {
                    if (isPhotoPickerAvailable()) {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                    }
                    true
                }
                R.id.add_audio -> {
                    // Handle dashboard icon press
                    true
                }
                R.id.add_mentions -> {
                    // Handle dashboard icon press
                    true
                }
                else -> false
            }
        }

        clear.setOnClickListener {
            viewModel.clearAttachment()
        }

        addPlace.setOnClickListener {
            findNavController().navigate(R.id.action_newPostFragment_to_mapFragment)
        }

        save.setOnClickListener {
            val coordinates = getCoordsData()
            if (checkFields() && checkCoordinate(coordinates)) {
                if (userViewModel.isLogin()) {
                    viewModel.save(
                        contentField.text.toString(),
                        coordinates,
                        linkField.text.toString().ifBlank { null },
                    )
                } else DialogManager.errorAuthDialog(this@NewPostFragment)
            } else {
                Toast.makeText(context, "check fields", Toast.LENGTH_SHORT).show()
            }
            viewModel.clearData()
        }
    }

    private fun checkFields(): Boolean = with(binding) {
        return (!contentField.text.isNullOrBlank())
    }

    private fun checkCoordinate(coordinates: Coordinates?): Boolean {
        if (coordinates == null) return true
        try {
            val lat = coordinates.lat.trim().toDouble()
            val lon = coordinates.longitude.trim().toDouble()
            if (lat > MAX_LATITUDE || lat < MIN_LATITUDE) return false
            if (lon > MAX_LONGITUDE || lon < MIN_LONGITUDE) return false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun getCoordsData(): Coordinates? = with(binding) {
        return if (latField.text.isNullOrBlank() || lonField.text.isNullOrBlank()) null
            else Coordinates(latField.text?.trim().toString(), lonField.text?.trim().toString())
    }


    companion object {
        var Bundle.postId: Long by LongArg
        const val MAX_LATITUDE = 90.0
        const val MIN_LATITUDE = -90.0
        const val MAX_LONGITUDE = 180.0
        const val MIN_LONGITUDE = -180.0
    }
}