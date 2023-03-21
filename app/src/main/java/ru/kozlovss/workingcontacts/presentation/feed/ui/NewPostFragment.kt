package ru.kozlovss.workingcontacts.presentation.feed.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.FragmentNewPostBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.feed.viewmodel.FeedViewModel


@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val viewModel: FeedViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentNewPostBinding
    lateinit var post: Post

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
        binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        setContent()
        addBackPressedAction()
        subscribe()
        setListeners()
        binding.content.requestFocus()
        binding.buttonScrollView.isHorizontalScrollBarEnabled = false

        return binding.root
    }

    private fun addBackPressedAction() {
        val callbackNoEdit = object : OnBackPressedCallback(viewModel.edited.value?.id != 0L) {
            override fun handleOnBackPressed() {
                viewModel.clearEdited()
                viewModel.clearDraft()
                findNavController().navigateUp()
            }
        }
        val callbackWithDraft = object : OnBackPressedCallback(viewModel.edited.value?.id == 0L) {
            override fun handleOnBackPressed() {
                if (binding.content.toString().trim().isNotBlank()) {
                    viewModel.draftContent.value = binding.content.text.toString()
                }
                findNavController().navigateUp()
            }
        }
        val backPressedDispatcher = requireActivity().onBackPressedDispatcher
        backPressedDispatcher.addCallback(viewLifecycleOwner, callbackNoEdit)
        backPressedDispatcher.addCallback(viewLifecycleOwner, callbackWithDraft)
    }

    private fun setContent() {
        if (viewModel.edited.value?.id == 0L) {
            if (viewModel.draftContent.value.toString().isNotBlank()) {
                viewModel.draftContent.value.let(binding.content::setText)
            }
        } else {
            post = viewModel.edited.value!!
            with(binding) {
                content.setText(post.content)
//                post.attachment?.let {
//                    //todo необходимо выяснить, как выгрузить картинку из сервера в photo
//                        ImagePicker.Builder(this)
//                            .cameraOnly()
//                            .maxResultSize(2048, 2048)
//                            .createIntent(imageLauncher::launch)
//                          Glide.with(preview)
//                            .load(PostRepositoryImpl.getImageUrl(post.attachment.url))
//                            .placeholder(R.drawable.ic_update_avatar)
//                            .error(R.drawable.ic_error_avatar)
//                            .timeout(10_000)
//                            .into(preview)
//                        val uri = it.url
//                        viewModel.savePhoto(uri, uri?.toFile())
//                }
                if (post.attachment != null) {
                    preview.visibility = View.VISIBLE
                } else {
                    preview.visibility = View.GONE
                }
            }
        }
    }

    private fun subscribe() {
        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenCreated {
            viewModel.photo.collect {
                with(binding) {
                    imageGroup.isVisible = it?.uri != null
                    preview.setImageURI(it?.uri)
                }
            }
        }
    }

    private fun setListeners() = with(binding) {
        takePhoto.setOnClickListener {
//            ImagePicker.Builder(this@NewPostFragment)
//                .cameraOnly()
//                .maxResultSize(2048, 2048)
//                .createIntent(imageLauncher::launch)
        }

        addPhoto.setOnClickListener {
            if (isPhotoPickerAvailable()) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
//            ImagePicker.Builder(this@NewPostFragment)
//                .galleryOnly()
//                .maxResultSize(2048, 2048)
//                .createIntent(imageLauncher::launch)
        }

        addVideo.setOnClickListener {
            if (isPhotoPickerAvailable()) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
        }

        clear.setOnClickListener {
            viewModel.clearPhoto()
        }

        done.setOnClickListener {
            val content = binding.content.text.toString()
            if (content.isNotBlank()) {
                if (userViewModel.isLogin()) {
                    viewModel.changeContentAndSave(content)
                } else DialogManager.errorAuthDialog(this@NewPostFragment)
            } else {
                viewModel.clearEdited()
            }
            viewModel.clearDraft()
            //AndroidUtils.hideKeyboard(requireView())
        }

        preview.setOnClickListener {
//            post.attachment?.let {
//                findNavController().navigate(
//                    R.id.action_newPostFragment_to_imageFragment,
//                    Bundle().apply { imageUrlArg = it.url }
//                )
//            }
        }
    }
}