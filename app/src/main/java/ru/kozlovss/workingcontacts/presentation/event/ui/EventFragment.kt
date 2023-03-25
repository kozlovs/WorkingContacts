package ru.kozlovss.workingcontacts.presentation.event.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.Attachment
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.databinding.FragmentEventBinding
import ru.kozlovss.workingcontacts.domain.util.DialogManager
import ru.kozlovss.workingcontacts.domain.util.Formatter
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.presentation.auth.viewmodel.UserViewModel
import ru.kozlovss.workingcontacts.presentation.event.model.EventModel
import ru.kozlovss.workingcontacts.presentation.event.viewmodel.EventViewModel
import ru.kozlovss.workingcontacts.presentation.events.viewmodel.EventsViewModel
import ru.kozlovss.workingcontacts.presentation.video.VideoFragment.Companion.url

@AndroidEntryPoint
class EventFragment : Fragment() {
    private val eventsViewModel: EventsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val eventViewModel: EventViewModel by viewModels()
    private lateinit var binding: FragmentEventBinding
    private var id: Long? = null

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

        subscribe()
        setListeners()
        eventViewModel.updateData(id)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        eventViewModel.clearData()
    }

    private fun subscribe() {
        lifecycleScope.launchWhenCreated {
            eventViewModel.data.collect { event ->
                event?.let { updateUi(it) }
            }
        }

        lifecycleScope.launchWhenCreated {
            eventViewModel.state.collectLatest { state ->
                binding.progress.isVisible = state is EventModel.State.Loading
                binding.cardLayout.isVisible = state is EventModel.State.Idle
                binding.errorLayout.isVisible = state is EventModel.State.Error
            }
        }

        eventsViewModel.edited.observe(viewLifecycleOwner) {
            if (it.id != 0L) {
                findNavController().navigate(
                    R.id.action_eventFragment_to_newEventFragment
                )
            }
        }
    }

    private fun updateUi(event: Event) = with(binding) {
        author.text = event.author
        authorJob.text = event.authorJob
        published.text = Formatter.localDateTimeToPostDateFormat(event.published)
        if (event.link != null) {
            link.visibility = View.VISIBLE
            link.text = event.link
        } else {
            link.visibility = View.GONE
        }
        content.text = event.content
        like.isChecked = event.likedByMe
        like.text = Formatter.numberToShortFormat(event.likeOwnerIds.size)
        menu.isVisible = event.ownedByMe

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
                    videoLayout.visibility = View.GONE
                    audio.visibility = View.GONE
                }
                Attachment.Type.AUDIO -> {
                    audio.visibility = View.VISIBLE
                    image.visibility = View.GONE
                    videoLayout.visibility = View.GONE
                }
                Attachment.Type.VIDEO -> {
                    videoLayout.visibility = View.VISIBLE
                    val uri = Uri.parse(attachment.url)
                    video.setVideoURI(uri)
                    video.seekTo(1)
                    image.visibility = View.GONE
                    audio.visibility = View.GONE
                }
                else -> {
                    image.visibility = View.GONE
                    videoLayout.visibility = View.GONE
                    audio.visibility = View.GONE
                }
            }
        } else {
            image.visibility = View.GONE
            videoLayout.visibility = View.GONE
            audio.visibility = View.GONE
        }
    }

    private fun setListeners() = with(binding) {
        like.setOnClickListener {
            if (userViewModel.isLogin()) {
                eventViewModel.likeById(id)
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.like,
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
                                    eventsViewModel.edit(event)
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
                findNavController().navigate(R.id.action_eventFragment_to_videoFragment,
                    Bundle().apply { url = it.url })
            }
        }

        switchButton.setOnClickListener {
            eventViewModel.data.value?.let { event ->
                eventsViewModel.switchAudio(event)
            }
        }
    }


    companion object {
        var Bundle.id: Long by LongArg
    }
}