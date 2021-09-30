package com.zopnote.android.merchant.dailyindent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private DailyIndentViewModel viewmodel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewmodel = DailyIndentActivity.obtainViewModel(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = viewmodel.calender;
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        long date = viewmodel.calender.getTime().getTime();
        System.out.println("DEBUGTRACEX "+ String.valueOf(date));

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
     //   datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
     //   datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        viewmodel.calender.set(year, month, dayOfMonth, 0 , 0 , 0);
        viewmodel.calender.set(Calendar.MILLISECOND, 0);
        viewmodel.dateChanged.setValue(true);
        long date = viewmodel.calender.getTime().getTime();
        System.out.println("DEBUGTRACEX "+String.valueOf(date));
        System.out.println("DEBUGTRACEX Day"+String.valueOf(viewmodel.calender.getTime().getDay()));
        //1630218600000
        //1630261800000
    }
}
