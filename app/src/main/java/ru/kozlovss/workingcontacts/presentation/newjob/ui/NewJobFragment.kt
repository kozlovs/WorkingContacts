package ru.kozlovss.workingcontacts.presentation.newjob.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kozlovss.workingcontacts.databinding.FragmentNewJobBinding
import ru.kozlovss.workingcontacts.presentation.newjob.model.NewJobModel
import ru.kozlovss.workingcontacts.presentation.newjob.viewmodel.NewJobViewModel
import ru.kozlovss.workingcontacts.presentation.newjob.viewmodel.NewJobViewModel.Event.*
import java.time.Instant
import java.time.ZoneId

@AndroidEntryPoint
class NewJobFragment : Fragment() {
    private lateinit var binding: FragmentNewJobBinding
    private val viewModel: NewJobViewModel by activityViewModels()
    private lateinit var datePicker: MaterialDatePicker<Long>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewJobBinding.inflate(inflater, container, false)
        initDatePiker()
        subscribe()
        setListeners()

        return binding.root
    }

    private fun initDatePiker() {
        datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
    }

    private fun subscribe() = with(binding) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    cardJob.isVisible =
                        state is NewJobModel.State.Idle
                    save.isVisible =
                        state is NewJobModel.State.Idle
                    progress.isVisible = state is NewJobModel.State.Loading
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
                        )
                            .show()
                        is ShowToast -> Toast.makeText(context, it.text, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setListeners() = with(binding) {
        startField.setOnClickListener {
            datePicker.show(parentFragmentManager, START_TAG)
        }

        finishField.setOnClickListener {
            datePicker.show(parentFragmentManager, FINISH_TAG)
        }

        save.setOnClickListener {
            if (checkFields()) {
                viewModel.save(
                    nameField.text.toString(),
                    positionField.text.toString(),
                    startField.text.toString(),
                    finishField.text.toString().ifBlank { null },
                    linkField.text.toString().ifBlank { null },
                )
            } else {
                Toast.makeText(context, "check fields", Toast.LENGTH_SHORT).show()
            }
        }

        datePicker.addOnPositiveButtonClickListener {
            val date = Instant
                .ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString()
            when (datePicker.tag) {
                START_TAG -> startField.setText(date)
                FINISH_TAG -> finishField.setText(date)
            }
        }
    }

    private fun checkFields(): Boolean = with(binding) {
        return !(nameField.text.isNullOrBlank() ||
                positionField.text.isNullOrBlank() ||
                startField.text.isNullOrBlank())
    }

    companion object {
        private const val START_TAG = "start"
        private const val FINISH_TAG = "finish"
    }
}