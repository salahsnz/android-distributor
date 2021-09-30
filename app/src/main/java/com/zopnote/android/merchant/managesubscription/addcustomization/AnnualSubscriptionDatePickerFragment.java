package com.zopnote.android.merchant.managesubscription.addcustomization;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.zopnote.android.merchant.util.FormatUtil;

import java.util.Calendar;

public class AnnualSubscriptionDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private AddCustomizationViewModel viewmodel;
    public static final int FLAG_START_DATE = 0;
    public static final int FLAG_END_DATE = 1;
    private int flag = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewmodel = AddCustomizationActivity.obtainViewModel(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = FormatUtil.getLocalCalenderNoTime();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        setMinimumDate(datePickerDialog);
        return datePickerDialog;
    }

    private void setMinimumDate(DatePickerDialog datePickerDialog) {
        if (flag == FLAG_END_DATE){
            if(viewmodel.annualSubscriptionStartDateCalender != null){
                datePickerDialog.getDatePicker().setMinDate(viewmodel.annualSubscriptionStartDateCalender.getTimeInMillis());
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (flag == FLAG_START_DATE) {
            if(viewmodel.annualSubscriptionStartDateCalender == null){
                viewmodel.annualSubscriptionStartDateCalender = FormatUtil.getLocalCalender();
            }
            viewmodel.annualSubscriptionStartDateCalender.set(year, month, dayOfMonth, 0, 0, 0);
            viewmodel.annualSubscriptionStartDateCalender.set(Calendar.MILLISECOND, 0);
            viewmodel.annualSubscriptionStartDateChanged.setValue(true);
        } else if (flag == FLAG_END_DATE) {
            if(viewmodel.annualSubscriptionEndDateCalender == null){
                viewmodel.annualSubscriptionEndDateCalender = FormatUtil.getLocalCalenderNoTime();
            }
            viewmodel.annualSubscriptionEndDateCalender.set(year, month, dayOfMonth, 0, 0, 0);
            viewmodel.annualSubscriptionEndDateCalender.set(Calendar.MILLISECOND, 0);
            viewmodel.annualSubscriptionEndDateChanged.setValue(true);
        }
    }

    public void setFlag(int i) {
        flag = i;
    }

}
