package ru.kozlovss.workingcontacts.presentation.newpost.ui

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.databinding.FragmentNewPostBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.domain.util.PermissionManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.map.ui.MapFragment
import ru.kozlovss.workingcontacts.presentation.map.ui.MapFragment.Companion.sourcePageTag
import ru.kozlovss.workingcontacts.presentation.newpost.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.newpost.adapter.UsersPreviewAdapter
import ru.kozlovss.workingcontacts.presentation.newpost.model.NewPostModel
import ru.kozlovss.workingcontacts.presentation.newpost.viewmodel.NewPostViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.viewmodel.NewPostViewModel.Event.*
import ru.kozlovss.workingcontacts.presentation.userslist.ui.UserBottomSheetFragment

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private val viewModel: NewPostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentNewPostBinding
    private lateinit var bottomSheet: UserBottomSheetFragment
    private lateinit var adapter: UsersPreviewAdapter

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.photo_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Activity.RESULT_OK -> {
                    val uri = it.data?.data
                    viewModel.saveAttachment(uri, uri?.toFile(), Attachment.Type.IMAGE)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.postId?.let { viewModel.getData(it) }
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        bottomSheet = UserBottomSheetFragment.newInstance()
        initAdapter()
        subscribe()
        addBackPressedAction()
        setListeners()

        return binding.root
    }

    private fun initAdapter() {
        adapter = UsersPreviewAdapter(object : OnInteractionListener {
            override fun onRemove(user: User) {
                viewModel.removeMention(user)
            }
        })
        binding.mentions.adapter = adapter
    }

    private fun addBackPressedAction() = with(binding) {
        val callbackExit = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.postId.value != null) {
                    viewModel.clearData()
                } else {
                    val content = contentField.text?.trim().toString()
                    val link = linkField.text?.trim().toString()
                    if (content.isNotBlank()) {
                        viewModel.makeDraft(
                            content,
                            Coordinates.getDataFromFields(
                                latField.text.toString(),
                                lonField.text.toString()
                            ),
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.content.collect {
                    it?.let { contentField.setText(it) }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.link.collect {
                    it?.let { linkField.setText(it) }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.coordinates.collect {
                    it?.let {
                        latField.setText(it.lat)
                        lonField.setText(it.longitude)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.attachmentRemote.collect {
                    attachmentGroup.isVisible = it != null
                    it?.let {
                        attachmentType.text = it.type.toString()
                        when (it.type) {
                            Attachment.Type.IMAGE -> {
                                Glide.with(preview)
                                    .load(it.url)
                                    .placeholder(R.drawable.baseline_update_24)
                                    .error(R.drawable.baseline_error_outline_24)
                                    .timeout(10_000)
                                    .into(preview)
                                preview.isVisible = true
                                audioIcon.isVisible = false
                            }

                            Attachment.Type.VIDEO -> {
                                Glide.with(preview)
                                    .load(it.url)
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
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mentions.collect {
                    mentions.isVisible = it.isNotEmpty()
                    adapter.submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    cardPost.isVisible = state is NewPostModel.State.Idle
                    bottomAppBar.isVisible = state is NewPostModel.State.Idle
                    save.isVisible = state is NewPostModel.State.Idle
                    progress.isVisible = state is NewPostModel.State.Loading
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect {
                    when (it) {
                        CreateNewItem -> findNavController().navigateUp()
                        is AddedMention -> Toast.makeText(
                            context,
                            getString(R.string.added_mention_of, it.userName),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setListeners() = with(binding) {
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.take_photo -> {
                    takePhoto()
                    true
                }

                R.id.add_photo -> {
                    addPhoto()
                    true
                }

                R.id.add_mentions -> {
                    addMentions()
                    true
                }

                else -> false
            }

        }

        topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
            viewModel.clearData()
        }

        clear.setOnClickListener {
            viewModel.clearAttachment()
        }

        addPlace.setOnClickListener {
            findNavController().navigate(R.id.action_global_mapFragment,
                Bundle().apply {
                    sourcePageTag = MapFragment.Companion.SourcePage.NEW_POST.toString()
                })
        }

        save.setOnClickListener {
            val coordinates = Coordinates.getDataFromFields(
                latField.text.toString(),
                lonField.text.toString()
            )
            if (checkFields() && Coordinates.check(coordinates)) {
                if (userViewModel.isLogin()) {
                    viewModel.save(
                        contentField.text.toString(),
                        coordinates,
                        linkField.text.toString().ifBlank { null },
                    )
                } else DialogManager.errorAuthDialog(this@NewPostFragment)
            } else {
                Toast.makeText(context, getString(R.string.check_fields), Toast.LENGTH_SHORT).show()
            }
            viewModel.clearData()
        }
    }

    private fun takePhoto() {
        if (PermissionManager.checkImagePermission(requireActivity())) {
            ImagePicker.Builder(this@NewPostFragment)
                .cameraOnly()
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent(imageLauncher::launch)
        } else {
            PermissionManager.requestImagePermission(requireActivity())
        }
    }

    private fun addPhoto() {
        if (PermissionManager.checkImagePermission(requireActivity())) {
            ImagePicker.Builder(this@NewPostFragment)
                .galleryOnly()
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent(imageLauncher::launch)
        } else {
            PermissionManager.requestImagePermission(requireActivity())
        }
    }

    private fun addMentions() {
        bottomSheet.show(
            requireActivity().supportFragmentManager,
            UserBottomSheetFragment.NEW_POST_TAG
        )
    }

    private fun checkFields(): Boolean = with(binding) {
        return (!contentField.text.isNullOrBlank())
    }

    companion object {
        var Bundle.postId: Long by LongArg
    }
}