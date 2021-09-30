package com.zopnote.android.merchant.updateinvoice;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;


import java.util.Calendar;

public class InvoiceDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private UpdateInvoiceViewModel viewmodel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewmodel = UpdateInvoiceActivity.obtainViewModel(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = viewmodel.InvStatusDateChangeCalender;
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        viewmodel.InvStatusDateChangeCalender.set(year, month, dayOfMonth, 0 , 0 , 0);
        viewmodel.InvStatusDateChangeCalender.set(Calendar.MILLISECOND, 0);
        viewmodel.dateChanged.setValue(true);
    }
}
