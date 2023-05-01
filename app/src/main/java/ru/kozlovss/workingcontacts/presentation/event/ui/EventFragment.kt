package ru.kozlovss.workingcontacts.presentation.event.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.entity.Attachment
import ru.kozlovss.workingcontacts.entity.Event
import ru.kozlovss.workingcontacts.entity.User
import ru.kozlovss.workingcontacts.databinding.FragmentEventBinding
import ru.kozlovss.workingcontacts.presentation.util.DialogManager
import ru.kozlovss.workingcontacts.presentation.util.Formatter
import ru.kozlovss.workingcontacts.presentation.util.LongArg
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.event.adapter.UsersPreviewAdapter
import ru.kozlovss.workingcontacts.presentation.event.model.EventModel
import ru.kozlovss.workingcontacts.presentation.event.viewmodel.EventViewModel
import ru.kozlovss.workingcontacts.presentation.events.viewmodel.EventsViewModel
import ru.kozlovss.workingcontacts.presentation.map.ui.MapFragment.Companion.lat
import ru.kozlovss.workingcontacts.presentation.map.ui.MapFragment.Companion.lon
import ru.kozlovss.workingcontacts.presentation.newevent.ui.NewEventFragment.Companion.eventId
import ru.kozlovss.workingcontacts.presentation.video.ui.VideoFragment.Companion.url

@AndroidEntryPoint
class EventFragment : Fragment() {
    private val eventsViewModel: EventsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val eventViewModel: EventViewModel by viewModels()
    private var binding: FragmentEventBinding? = null
    private var id: Long? = null
    private lateinit var adapter: UsersPreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventBinding.inflate(
            inflater,
            container,
            false
        )

        id = arguments?.id


        adapter = UsersPreviewAdapter()
        binding!!.speakersList.adapter = adapter
        subscribe()
        setListeners()
        eventViewModel.updateData(id)

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        eventViewModel.clearData()
        binding = null
    }

    private fun subscribe() = with(binding!!) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.data.collect { event ->
                    event?.let {
                        updateUi(it)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.state.collectLatest { state ->
                    progress.isVisible = state is EventModel.State.Loading
                    cardLayout.isVisible = state is EventModel.State.Idle
                    errorLayout.isVisible = state is EventModel.State.Error
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.speakersVisibility.collect {
                    speakersCard.isVisible = it && !eventViewModel.data.value?.speakerIds.isNullOrEmpty()
                    speakersSelectorIcon.isChecked = it
                }
            }
        }
    }

    private fun updateUi(event: Event) = with(binding!!) {
        author.text = event.author
        authorJob.text = event.authorJob
        published.text = Formatter.localDateTimeToPostDateFormat(event.published)
        link.isVisible = event.link != null
        event.link?.let { link.text = event.link }
        content.text = event.content
        type.text = event.type.toString()
        if (event.type == Event.Type.ONLINE) typeIcon.setImageResource(R.drawable.baseline_online_24)
        else typeIcon.setImageResource(R.drawable.baseline_people_24)
        dateTime.text = Formatter.localDateTimeToPostDateFormat(event.datetime)
        place.isVisible = event.coords != null
        speakersCount.text = event.speakerIds.size.toString()
        like.isChecked = event.likedByMe
        like.text = Formatter.numberToShortFormat(event.likeOwnerIds.size)
        participate.isChecked = event.participatedByMe
        participate.text = Formatter.numberToShortFormat(event.participantsIds.size)
        menu.isVisible = event.ownedByMe
        speakersSelectorIcon.isVisible = event.speakerIds.isNotEmpty()
        adapter.submitList(event.speakerIds.map {
            val preview = event.users[it]!!
            User(
                it,
                preview.name,
                preview.name,
                preview.avatar
            )
        })

        if (event.authorAvatar != null) {
            Glide.with(avatar)
                .load(event.authorAvatar)
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(avatar)
        } else {
            avatar.setImageResource(R.drawable.baseline_person_outline_24)
        }

        val attachment = event.attachment
        if (attachment != null) {
            when (attachment.type) {
                Attachment.Type.IMAGE -> {
                    image.visibility = View.VISIBLE
                    Glide.with(image)
                        .load(attachment.url)
                        .transform(RoundedCorners(30))
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(image)
                    video.visibility = View.GONE
                    videoIcon.visibility = View.GONE
                    audioButton.visibility = View.GONE
                    audioName.visibility = View.GONE
                }
                Attachment.Type.AUDIO -> {
                    audioButton.visibility = View.VISIBLE
                    audioName.visibility = View.VISIBLE
                    image.visibility = View.GONE
                    videoIcon.visibility = View.GONE
                    video.visibility = View.GONE
                }
                Attachment.Type.VIDEO -> {
                    video.visibility = View.VISIBLE
                    videoIcon.visibility = View.VISIBLE
                    Glide.with(video)
                        .load(attachment.url)
                        .transform(RoundedCorners(30))
                        .placeholder(R.drawable.baseline_update_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .timeout(10_000)
                        .into(video)
                    image.visibility = View.GONE
                    audioButton.visibility = View.GONE
                    audioName.visibility = View.GONE
                }
            }
        } else {
            image.visibility = View.GONE
            video.visibility = View.GONE
            videoIcon.visibility = View.GONE
            audioButton.visibility = View.GONE
            audioName.visibility = View.GONE
        }
    }

    private fun setListeners() = with(binding!!) {
        like.setOnClickListener {
            if (userViewModel.isLogin()) {
                eventViewModel.likeById(id)
                ObjectAnimator.ofPropertyValuesHolder(
                    like,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            } else DialogManager.errorAuthDialog(this@EventFragment)
        }

        participate.setOnClickListener {
            if (userViewModel.isLogin()) {
                eventViewModel.participateById(id)
                ObjectAnimator.ofPropertyValuesHolder(
                    participate,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F)
                ).start()
            } else DialogManager.errorAuthDialog(this@EventFragment)
        }

        share.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, eventViewModel.data.value?.content)
                type = "text/plain"
            }
            val shareIntent =
                Intent.createChooser(intent, getString(R.string.chooser_share_event))
            startActivity(shareIntent)
        }

        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options_event_menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            if (userViewModel.isLogin()) {
                                id?.let { id ->
                                    eventsViewModel.removeById(id)
                                }
                            } else DialogManager.errorAuthDialog(this@EventFragment)
                            true
                        }
                        R.id.edit -> {
                            if (userViewModel.isLogin()) {
                                eventViewModel.data.value?.let { event ->
                                    findNavController().navigate(R.id.action_global_newEventFragment,
                                        Bundle().apply { eventId = event.id })
                                }
                            } else DialogManager.errorAuthDialog(this@EventFragment)
                            true
                        }
                        else -> false
                    }

                }
            }.show()
        }

        video.setOnClickListener {
            eventViewModel.data.value?.attachment?.let {
                findNavController().navigate(R.id.action_global_videoFragment,
                    Bundle().apply { url = it.url })
            }
        }

        audioButton.setOnClickListener {
            eventViewModel.data.value?.let { event ->
                eventsViewModel.switchAudio(event)
            }
        }

        speakersSelector.setOnClickListener {
            eventViewModel.switchSpeakersVisibility()
        }

        place.setOnClickListener {
            eventViewModel.data.value?.coords?.let { coords ->
                findNavController().navigate(R.id.action_global_mapFragment, Bundle().apply {
                    lat = coords.lat
                    lon = coords.longitude
                })
            }
        }
    }


    companion object {
        var Bundle.id: Long by LongArg
    }
}