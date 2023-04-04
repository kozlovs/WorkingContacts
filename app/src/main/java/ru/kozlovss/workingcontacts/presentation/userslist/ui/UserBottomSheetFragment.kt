package ru.kozlovss.workingcontacts.presentation.userslist.ui

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.databinding.FragmentUserBottomSheetBinding
import ru.kozlovss.workingcontacts.presentation.userslist.adapter.OnInteractionListener
import ru.kozlovss.workingcontacts.presentation.userslist.adapter.UsersAdapter
import ru.kozlovss.workingcontacts.presentation.userslist.viewmodel.UsersViewModel

@AndroidEntryPoint
class UserBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentUserBottomSheetBinding
    private lateinit var adapter: UsersAdapter
    private val viewModel: UsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBottomSheetBinding.inflate(inflater, container, false)
        adapter = UsersAdapter(object : OnInteractionListener {
            override fun onSelect(user: User) {
                TODO("Not yet implemented")
            }
        })
        binding.list.layoutManager = GridLayoutManager(context, COLUMN_COUNT)
        binding.list.adapter = adapter


        subscribe()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userData.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
        const val COLUMN_COUNT = 4

        @JvmStatic
        fun newInstance() = UserBottomSheetFragment()
    }
}