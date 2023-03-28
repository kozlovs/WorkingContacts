package ru.kozlovss.workingcontacts.presentation.newjob.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.databinding.FragmentNewJobBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter
import ru.kozlovss.workingcontacts.domain.util.LongArg
import ru.kozlovss.workingcontacts.presentation.newjob.viewmodel.NewJobViewModel
import java.time.Instant
import java.time.ZoneId

@AndroidEntryPoint
class NewJobFragment : Fragment() {
    private var jobId: Long? = null
    private lateinit var binding: FragmentNewJobBinding
    private val viewModel: NewJobViewModel by activityViewModels()
    private lateinit var datePicker: MaterialDatePicker<Long>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        jobId = arguments?.jobId
        jobId?.let { viewModel.getData(it) }
        binding = FragmentNewJobBinding.inflate(inflater, container, false)
        initDataPiker()
        subscribe()
        setListeners()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearData()
    }

    private fun initDataPiker() {
        datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
    }

    private fun subscribe() {
        lifecycleScope.launchWhenCreated {
            viewModel.jobData.collect {
                updateUi(it)
            }
        }
    }

    private fun updateUi(job: Job?) = with(binding) {
        job?.let {
            nameField.setText(job.name)
            positionField.setText(job.position)
            startField.setText(Formatter.localDateTimeToJobDateFormat(job.start))
            job.finish?.let { finishField.setText(Formatter.localDateTimeToJobDateFormat(it)) }
            job.link?.let { linkField.setText(it) }
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
        var Bundle.jobId: Long by LongArg
        private const val START_TAG = "start"
        private const val FINISH_TAG = "finish"
    }
}