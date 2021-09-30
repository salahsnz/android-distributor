package com.zopnote.android.merchant.viewcustomer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.zopnote.android.merchant.addsubscription.AddSubscriptionActivity;
import com.zopnote.android.merchant.addsubscription.AddSubscriptionViewModel;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.Validatable;

import java.util.Calendar;

public class CheckOutDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, Validatable {
    private ViewCustomerViewModel viewmodel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewmodel = ViewCustomerActivity.obtainViewModel(getActivity());
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
        viewmodel.checkOutDateCalender = FormatUtil.getLocalCalenderNoTime();
        viewmodel.checkOutDateCalender.set(year, month, dayOfMonth, 0,0,0);
        viewmodel.checkOutDateCalender.set(Calendar.MILLISECOND, 0);
        viewmodel.checkOutDateChanged.setValue(true);
    }

    @Override
    public boolean validate() {
        if(viewmodel.checkOutDateCalender != null){
            return true;
        }
        return false;
    }
}
