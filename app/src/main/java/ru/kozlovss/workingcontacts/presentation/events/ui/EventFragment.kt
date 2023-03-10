package ru.kozlovss.workingcontacts.presentation.events.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
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
import ru.kozlovss.workingcontacts.domain.util.Formatter
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.presentation.events.viewmodel.EventViewModel

@AndroidEntryPoint
class EventFragment : Fragment() {
    private val viewModel: EventViewModel by activityViewModels()
    lateinit var event: Event
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

        return binding.root
    }

    private fun subscribe() {
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                val requestEvent = id?.let { viewModel.getById(it)}
                requestEvent?.let {
                    event = requestEvent
                    updateView()
                } ?: findNavController().navigateUp()
            }
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id == 0L) {
                return@observe
            }
            findNavController().navigate(
                R.id.action_eventFragment_to_newEventFragment,
            )
        }
    }

    private fun updateView() {
        binding.apply {
            author.text = event.author
            authorJob.text = event.authorJob
            published.text = event.published
            if (event.link != null) {
                link.visibility = View.VISIBLE
                link.text = event.link
            } else {
                link.visibility = View.GONE
            }
            content.text = event.content
            like.isChecked = event.likedByMe
            like.text = Formatter.numberToShortFormat(event.likeOwnerIds.size)

            Glide.with(binding.avatar)
                .load(event.authorAvatar)
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(binding.avatar)

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
                        audio.visibility = View.GONE
                    }
                    Attachment.Type.AUDIO -> {
                        audio.visibility = View.VISIBLE
                        image.visibility = View.GONE
                        video.visibility = View.GONE
                    }
                    Attachment.Type.VIDEO -> {
                        video.visibility = View.VISIBLE
                        image.visibility = View.GONE
                        audio.visibility = View.GONE
                    }
                    else -> {
                        image.visibility = View.GONE
                        video.visibility = View.GONE
                        audio.visibility = View.GONE
                    }
                }
            } else {
                image.visibility = View.GONE
                video.visibility = View.GONE
                audio.visibility = View.GONE
            }
        }
    }

    private fun setListeners() {
        binding.apply {

            like.setOnClickListener {
                if (viewModel.checkLogin(this@EventFragment)) {
                    viewModel.likeById(event.id)
                }
            }

            share.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.content)
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
                                viewModel.removeById(event.id)
                                true
                            }
                            R.id.edit -> {
                                viewModel.edit(event)
                                true
                            }
                            else -> false
                        }

                    }
                }.show()
            }

            video.setOnClickListener {
                if (event.attachment == null) return@setOnClickListener
                //val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.video))
                //startActivity(intent)
            }
        }
    }


    companion object {
        var Bundle.id: Long by LongArg
    }
}