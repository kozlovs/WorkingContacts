package ru.kozlovss.workingcontacts.presentation.userslist.ui

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.entity.User
import ru.kozlovss.workingcontacts.databinding.FragmentUserBottomSheetBinding
import ru.kozlovss.workingcontacts.presentation.newevent.viewmodel.NewEventViewModel
import ru.kozlovss.workingcontacts.presentation.newpost.viewmodel.NewPostViewModel
import ru.kozlovss.workingcontacts.presentation.userslist.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.userslist.adapter.UsersAdapter
import ru.kozlovss.workingcontacts.presentation.userslist.viewmodel.UsersViewModel

@AndroidEntryPoint
class UserBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentUserBottomSheetBinding? = null
    private var adapter: UsersAdapter? = null
    private val viewModel: UsersViewModel by viewModels()
    private val newPostViewModel: NewPostViewModel by activityViewModels()
    private val newEventViewModel: NewEventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBottomSheetBinding.inflate(inflater, container, false)
        initAdapter()
        subscribe()
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    private fun initAdapter() = with(binding!!) {
        adapter = UsersAdapter(object : OnInteractionListener {
            override fun onSelect(user: User) {
                when(tag) {
                    NEW_POST_TAG -> newPostViewModel.addMention(user)
                    NEW_EVENT_TAG -> newEventViewModel.addSpeaker(user)
                }
            }
        })
        list.layoutManager = GridLayoutManager(context, COLUMN_COUNT)
        list.adapter = adapter
    }

    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userData.collect {
                    adapter!!.submitList(it)
                }
            }
        }
    }

    companion object {
        const val NEW_POST_TAG = "NEW_POST_TAG"
        const val NEW_EVENT_TAG = "NEW_EVENT_TAG"
        const val COLUMN_COUNT = 4

        @JvmStatic
        fun newInstance() = UserBottomSheetFragment()
    }
}