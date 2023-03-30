package ru.kozlovss.workingcontacts.presentation.newpost.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.databinding.FragmentNewPostBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.model.NewPostModel
import ru.kozlovss.workingcontacts.presentation.newpost.viewmodel.NewPostViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.viewmodel.NewPostViewModel.Event.*


var storage_permissions = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE
)

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
var image_permissions_33 = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
var video_permissions_33 = arrayOf(Manifest.permission.READ_MEDIA_VIDEO)

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
var audio_permissions_33 = arrayOf(Manifest.permission.READ_MEDIA_AUDIO)

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val viewModel: NewPostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentNewPostBinding
    private val mediaStore = MediaStore()
    private var permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("MyLog", "Selected URI: $uri")

                val file = uri.toFile()
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
        arguments?.postId?.let { viewModel.getData(it) }
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        subscribe()
        addBackPressedAction()
        setListeners()

        return binding.root
    }

    private fun addBackPressedAction() {
        val callbackExit = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.postId.value != null) {
                    viewModel.clearData()
                } else {
                    val content = binding.contentField.text?.trim().toString()
                    val link = binding.linkField.text?.trim().toString()
                    if (content.isNotBlank()) {
                        viewModel.makeDraft(
                            content,
                            getCoordsData(),
                            link.ifBlank { null }
                        )
                    } else viewModel.clearData()
                }
                findNavController().navigateUp()
            }
        }
        val backPressedDispatcher = requireActivity().onBackPressedDispatcher
        backPressedDispatcher.addCallback(viewLifecycleOwner, callbackExit)
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
                    attachmentType.text = it.type.toString()
                    when (it.type) {
                        Attachment.Type.IMAGE -> {
                            Glide.with(preview)
                                .load(it.uri)
                                .placeholder(R.drawable.baseline_update_24)
                                .error(R.drawable.baseline_error_outline_24)
                                .timeout(10_000)
                                .into(preview)
                            preview.isVisible = true
                            audioIcon.isVisible = false
                        }
                        Attachment.Type.VIDEO -> {
                            Glide.with(preview)
                                .load(it.uri)
                                .placeholder(R.drawable.baseline_update_24)
                                .error(R.drawable.baseline_error_outline_24)
                                .timeout(10_000)
                                .into(preview)
                            preview.isVisible = true
                            audioIcon.isVisible = false
                        }
                        Attachment.Type.AUDIO -> {
                            preview.isVisible = false
                            audioIcon.isVisible = true
                        }
                    }
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
                    if (checkImagePermission()) {
                        //            ImagePicker.Builder(this@NewPostFragment)
//                .cameraOnly()
//                .maxResultSize(2048, 2048)
//                .createIntent(imageLauncher::launch)
                        true
                    } else {
                        requestImagePermission()
                        true
                    }

                }
                R.id.add_photo -> {
                    if (checkImagePermission()) {
                        if (isPhotoPickerAvailable(requireContext())) {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                        //            ImagePicker.Builder(this@NewPostFragment)
//                .galleryOnly()
//                .maxResultSize(2048, 2048)
//                .createIntent(imageLauncher::launch)
                        true
                    } else {
                        requestImagePermission()
                        true
                    }
                }
                R.id.add_video -> {
                    if (checkVideoPermission()) {
                        if (isPhotoPickerAvailable(requireContext())) {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        }
                        true
                    } else {
                        requestVideoPermission()
                        true
                    }
                }
                R.id.add_audio -> {
                    if (checkAudioPermission()) {

                        true
                    } else {
                        requestAudioPermission()
                        true
                    }
                }
                R.id.add_mentions -> {
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

    private fun checkImagePermission() = imagePermissions()
        .map { requireActivity().checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        .contains(false)

    private fun checkVideoPermission() = videoPermissions()
        .map { requireActivity().checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        .contains(false)

    private fun checkAudioPermission() = audioPermissions()
        .map { requireActivity().checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        .contains(false)

    private fun requestImagePermission() {
        ActivityCompat.requestPermissions(requireActivity(), imagePermissions(), 1)
    }

    private fun requestVideoPermission() {
        ActivityCompat.requestPermissions(requireActivity(), videoPermissions(), 1)
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(requireActivity(), audioPermissions(), 1)
    }

    private fun checkFields(): Boolean = with(binding) {
        return (!contentField.text.isNullOrBlank())
    }

    private fun imagePermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) image_permissions_33
        else storage_permissions

    private fun videoPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) video_permissions_33
        else storage_permissions

    private fun audioPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) audio_permissions_33
        else storage_permissions

    private fun makePermissionToast() {
        Toast.makeText(requireContext(), getString(R.string.need_permission), Toast.LENGTH_SHORT).show()
    }

    companion object {
        var Bundle.postId: Long by LongArg
        const val MAX_LATITUDE = 90.0
        const val MIN_LATITUDE = -90.0
        const val MAX_LONGITUDE = 180.0
        const val MIN_LONGITUDE = -180.0
    }
}