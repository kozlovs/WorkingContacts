package ru.kozlovss.workingcontacts.presentation.newevent.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.dto.Coordinates
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.databinding.FragmentNewEventBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.domain.util.PermissionManager
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.map.ui.MapFragment
import ru.kozlovss.workingcontacts.presentation.map.ui.MapFragment.Companion.sourcePageTag
import ru.kozlovss.workingcontacts.presentation.newevent.model.NewEventModel
import ru.kozlovss.workingcontacts.presentation.newevent.viewmodel.NewEventViewModel
import ru.kozlovss.workingcontacts.presentation.newevent.viewmodel.NewEventViewModel.LocalEvent.*
import ru.kozlovss.workingcontacts.presentation.newevent.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.newevent.adapter.UsersPreviewAdapter
import ru.kozlovss.workingcontacts.presentation.userslist.ui.UserBottomSheetFragment
import java.time.*

@AndroidEntryPoint
class NewEventFragment : Fragment() {
    private val viewModel: NewEventViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentNewEventBinding
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var timePicker: MaterialTimePicker
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.eventId?.let { viewModel.getData(it) }
        binding = FragmentNewEventBinding.inflate(inflater, container, false)
        bottomSheet = UserBottomSheetFragment.newInstance()
        initAdapter()
        initDatePiker()
        subscribe()
        addBackPressedAction()
        setListeners()

        return binding.root
    }

    private fun initAdapter() {
        adapter = UsersPreviewAdapter(object : OnInteractionListener {
            override fun onRemove(user: User) {
                viewModel.removeSpeaker(user)
            }
        })
        binding.speakers.adapter = adapter
    }

    private fun initDatePiker() {
        datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        timePicker = MaterialTimePicker.Builder()
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Select time")
            .build()
    }

    private fun addBackPressedAction() {
        val callbackExit = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.eventId.value != null) {
                    viewModel.clearData()
                } else {
                    val content = binding.contentField.text?.trim().toString()
                    val dataTime = getDataTimeData()
                    val link = binding.linkField.text?.trim().toString()
                    if (content.isNotBlank()) {
                        viewModel.makeDraft(
                            content,
                            dataTime,
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
                viewModel.dateTime.collect {
                    it?.let {
                        val dateTime = LocalDateTime.parse(it.substring(0, 16))
                        dateField.setText(dateTime.toLocalDate().toString())
                        timeField.setText(dateTime.toLocalTime().toString())
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.type.collect {
                    when (it) {
                        Event.Type.ONLINE -> {
                            onlineButton.isChecked = true
                        }

                        Event.Type.OFFLINE -> {
                            offlineButton.isChecked = true
                        }
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
                viewModel.speakers.collect {
                    speakers.isVisible = it.isNotEmpty()
                    adapter.submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    cardPost.isVisible = state is NewEventModel.State.Idle
                    bottomAppBar.isVisible = state is NewEventModel.State.Idle
                    save.isVisible = state is NewEventModel.State.Idle
                    progress.isVisible = state is NewEventModel.State.Loading
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect {
                    when (it) {
                        CreateNewItem -> findNavController().navigateUp()
                        is ShowSnackBar -> Snackbar.make(
                            binding.root,
                            it.text,
                            Snackbar.LENGTH_LONG
                        ).show()

                        is ShowToast -> Toast.makeText(
                            context,
                            it.text,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

                R.id.add_speakers -> {
                    addSpeaker()
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
                    sourcePageTag = MapFragment.Companion.SourcePage.NEW_EVENT.toString()
                })
        }

        save.setOnClickListener {
            val coordinates = getCoordsData()
            if (checkFields() && checkCoordinate(coordinates)) {
                if (userViewModel.isLogin()) {
                    viewModel.save(
                        contentField.text.toString(),
                        getDataTimeData(),
                        coordinates,
                        linkField.text.toString().ifBlank { null },
                    )
                } else DialogManager.errorAuthDialog(this@NewEventFragment)
            } else {
                Toast.makeText(context, getString(R.string.check_fields), Toast.LENGTH_SHORT).show()
            }
            viewModel.clearData()
        }

        dateField.setOnClickListener {
            datePicker.show(parentFragmentManager, null)
        }

        timeField.setOnClickListener {
            timePicker.show(parentFragmentManager, null)
        }

        datePicker.addOnPositiveButtonClickListener {
            val date = Instant
                .ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString()
            dateField.setText(date)
        }

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            timeField.setText(getString(R.string.time_field_format, hour, minute))
        }

        typeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.online_button -> viewModel.setType(Event.Type.ONLINE)
                R.id.offline_button -> viewModel.setType(Event.Type.OFFLINE)
            }
        }
    }

    private fun takePhoto() {
        if (PermissionManager.checkImagePermission(requireActivity())) {
            ImagePicker.Builder(this@NewEventFragment)
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
            ImagePicker.Builder(this@NewEventFragment)
                .galleryOnly()
                .crop()
                .maxResultSize(2048, 2048)
                .createIntent(imageLauncher::launch)
        } else {
            PermissionManager.requestImagePermission(requireActivity())
        }
    }

    private fun addSpeaker() {
        bottomSheet.show(
            requireActivity().supportFragmentManager,
            UserBottomSheetFragment.NEW_EVENT_TAG
        )
    }

    private fun checkCoordinate(coordinates: Coordinates?): Boolean {
        if (coordinates == null) return true
        try {
            val lat = coordinates.lat.trim().toDouble()
            val lon = coordinates.longitude.trim().toDouble()
            if (lat > Coordinates.MAX_LATITUDE || lat < Coordinates.MIN_LATITUDE) return false
            if (lon > Coordinates.MAX_LONGITUDE || lon < Coordinates.MIN_LONGITUDE) return false
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

    private fun getDataTimeData(): String? = with(binding) {
        return if (dateField.text.isNullOrBlank() || timeField.text.isNullOrBlank()) null
        else {
            val time = LocalTime.parse(timeField.text.toString())
            val date = LocalDate.parse(dateField.text.toString())
            return LocalDateTime.of(date, time).toString()
        }
    }

    private fun checkFields(): Boolean = with(binding) {
        return (!contentField.text.isNullOrBlank() &&
                !dateField.text.isNullOrBlank() &&
                !timeField.text.isNullOrBlank())
    }

    companion object {
        var Bundle.eventId: Long by LongArg
    }
}