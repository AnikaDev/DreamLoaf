package com.example.dreamloaf.production;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.dreamloaf.R;
import com.example.dreamloaf.databinding.FragmentCalendarBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long today = calendar.getTimeInMillis();
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker(today));
    }

    private void showDatePicker(long initialDate) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(initialDate)
                .setEnd(initialDate + (365 * 24 * 60 * 60 * 1000L));

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату")
                .setSelection(initialDate)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                navigateToProductionLog(selection);
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void navigateToProductionLog(long dateInMillis) {
        String selectedDate = formatDate(dateInMillis);
        Bundle args = new Bundle();
        args.putString("selected_date", selectedDate);

        Navigation.findNavController(requireView())
                .navigate(R.id.action_calendarFragment_to_productionLogFragment, args);
    }

    private String formatDate(long dateInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(dateInMillis));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}