package com.zopnote.android.merchant.addsubscription;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.Validatable;

import java.util.Calendar;

public class SubscriptionsStartDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, Validatable {
    private AddSubscriptionViewModel viewmodel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewmodel = AddSubscriptionActivity.obtainViewModel(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = FormatUtil.getLocalCalenderNoTime();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        viewmodel.startDateCalender = FormatUtil.getLocalCalenderNoTime();
        viewmodel.startDateCalender.set(year, month, dayOfMonth, 0,0,0);
        viewmodel.startDateCalender.set(Calendar.MILLISECOND, 0);
        viewmodel.startDateChanged.setValue(true);
    }

    @Override
    public boolean validate() {
        if(viewmodel.startDateCalender != null){
            return true;
        }
        return false;
    }
}
