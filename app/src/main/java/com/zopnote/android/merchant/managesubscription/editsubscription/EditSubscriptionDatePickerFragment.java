package com.zopnote.android.merchant.managesubscription.editsubscription;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.zopnote.android.merchant.util.FormatUtil;

import java.util.Calendar;

public class EditSubscriptionDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private EditSubscriptionViewModel viewmodel;
    public static final int FLAG_START_DATE = 0;
    public static final int FLAG_END_DATE = 1;
    private int flag = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewmodel = EditSubscriptionActivity.obtainViewModel(getActivity());
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
            if(viewmodel.startDateCalender != null){
                datePickerDialog.getDatePicker().setMinDate(viewmodel.startDateCalender.getTimeInMillis());
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (flag == FLAG_START_DATE) {
            viewmodel.startDateCalender.set(year, month, dayOfMonth, 0, 0, 0);
            viewmodel.startDateCalender.set(Calendar.MILLISECOND, 0);
            viewmodel.startDateChanged.setValue(true);
        } else if (flag == FLAG_END_DATE) {
            if(viewmodel.endDateCalender == null){
                viewmodel.endDateCalender = FormatUtil.getLocalCalenderNoTime();
            }
            viewmodel.endDateCalender.set(year, month, dayOfMonth, 0, 0, 0);
            viewmodel.endDateCalender.set(Calendar.MILLISECOND, 0);
            viewmodel.endDateChanged.setValue(true);
        }
    }

    public void setFlag(int i) {
        flag = i;
    }

}
