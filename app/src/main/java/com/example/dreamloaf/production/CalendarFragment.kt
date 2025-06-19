package com.example.dreamloaf.production

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import com.example.dreamloaf.R
import com.example.dreamloaf.databinding.FragmentCalendarBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CalendarFragment : androidx.fragment.app.Fragment() {
    private var binding: FragmentCalendarBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val today = calendar.getTimeInMillis()
        binding!!.btnSelectDate.setOnClickListener(View.OnClickListener { v: View? ->
            showDatePicker(
                today
            )
        })
    }

    private fun showDatePicker(initialDate: Long) {
        val constraintsBuilder: CalendarConstraints.Builder = CalendarConstraints.Builder()
            .setStart(initialDate)
            .setEnd(initialDate + (365 * 24 * 60 * 60 * 1000L))

        val datePicker: MaterialDatePicker<Long?> = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату")
            .setSelection(initialDate)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            navigateToProductionLog(selection!!)
        }

        datePicker.show(getParentFragmentManager(), "DATE_PICKER")
    }

    private fun navigateToProductionLog(dateInMillis: Long) {
        val selectedDate = formatDate(dateInMillis)
        val args = Bundle()
        args.putString("selected_date", selectedDate)

        findNavController(requireView())
            .navigate(R.id.action_calendarFragment_to_productionLogFragment, args)
    }

    private fun formatDate(dateInMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(dateInMillis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}