package com.zopnote.android.merchant.reports.ordersummarycustomerdetails;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.zopnote.android.merchant.util.FormatUtil;

import java.util.Calendar;
import java.util.Date;

public class OrderSummaryDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private OrderSummaryCustomerDetailsReportViewModel viewmodel;

    public static final int FLAG_START_DATE = 0;
    public static final int FLAG_END_DATE = 1;
    private int flag = 0;
    private Date startDate;
    private Date endDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewmodel = OrderSummaryCustomerDetailsReportActivity.obtainViewModel(getActivity());
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
        datePickerDialog.getDatePicker().setMinDate(startDate.getTime());
    }

    private void setMaximumDate(DatePickerDialog datePickerDialog) {
        datePickerDialog.getDatePicker().setMaxDate(endDate.getTime());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        if (flag == FLAG_START_DATE) {
            Log.d("CSD","START DATE YEAR: "+year+" MONTH "+month+ " DAY "+dayOfMonth);

            if(viewmodel.startDateCalender == null){
                viewmodel.startDateCalender = FormatUtil.getLocalCalenderNoTime();
            }
            viewmodel.startDateCalender.set(year, month, dayOfMonth, 0, 0, 0);
            viewmodel.startDateCalender.set(Calendar.MILLISECOND, 0);

            Log.d("CSD","GET TIME IN MILLIS: "+viewmodel.startDateCalender.getTimeInMillis());

            viewmodel.startDateChanged.setValue(true);
        } else if (flag == FLAG_END_DATE) {

            Log.d("CSD","END DATE YEAR: "+year+" MONTH "+month+ " DAY "+dayOfMonth);
            if(viewmodel.endDateCalender == null){
                viewmodel.endDateCalender = FormatUtil.getLocalCalenderNoTime();
            }
            viewmodel.endDateCalender.set(year, month, dayOfMonth, 0, 0, 0);
            viewmodel.endDateCalender.set(Calendar.MILLISECOND, 0);
            Log.d("CSD","GET TIME IN MILLIS: "+viewmodel.endDateCalender.getTimeInMillis());
            viewmodel.endDateChanged.setValue(true);
        }
    }

    public void setFlag(int i) {
        flag = i;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
