package com.zopnote.android.merchant.managesubscription.editpause;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.zopnote.android.merchant.util.FormatUtil;

import java.util.Calendar;
import java.util.Date;

public class EditPauseDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private EditPauseViewModel viewmodel;
    public static final int FLAG_START_DATE = 0;
    public static final int FLAG_END_DATE = 1;
    private int flag = 0;
    private Date subscriptionStartDate;
    private Date subscriptionEndDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewmodel = EditPauseActivity.obtainViewModel(getActivity());
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
        setMaximumDate(datePickerDialog);
        return datePickerDialog;
    }

    private void setMinimumDate(DatePickerDialog datePickerDialog) {
        if (flag == FLAG_START_DATE){
            datePickerDialog.getDatePicker().setMinDate(subscriptionStartDate.getTime());
        }

        if (flag == FLAG_END_DATE){
            if(viewmodel.pauseStartDateCalender != null){
                datePickerDialog.getDatePicker().setMinDate(viewmodel.pauseStartDateCalender.getTimeInMillis());
            }
        }
    }

    private void setMaximumDate(DatePickerDialog datePickerDialog) {
        if(subscriptionEndDate != null){
            if (flag == FLAG_START_DATE){
                datePickerDialog.getDatePicker().setMaxDate(subscriptionEndDate.getTime());
            }

            if (flag == FLAG_END_DATE){
                datePickerDialog.getDatePicker().setMaxDate(subscriptionEndDate.getTime());
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (flag == FLAG_START_DATE) {
            viewmodel.pauseStartDateCalender.set(year, month, dayOfMonth, 0, 0, 0);
            viewmodel.pauseStartDateCalender.set(Calendar.MILLISECOND, 0);
            viewmodel.startDateChanged.setValue(true);
        } else if (flag == FLAG_END_DATE) {
            if(viewmodel.pauseEndDateCalender == null){
                viewmodel.pauseEndDateCalender = FormatUtil.getLocalCalenderNoTime();
            }
            viewmodel.pauseEndDateCalender.set(year, month, dayOfMonth, 0 ,0, 0);
            viewmodel.pauseEndDateCalender.set(Calendar.MILLISECOND, 0);
            viewmodel.endDateChanged.setValue(true);
        }
    }

    public void setFlag(int i) {
        flag = i;
    }

    public void setSubscriptionStartDate(Date subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }
}
