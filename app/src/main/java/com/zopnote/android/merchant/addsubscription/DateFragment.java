package com.zopnote.android.merchant.addsubscription;


import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.DateFragBinding;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.Validatable;

public class DateFragment extends Fragment implements Validatable {
    private DateFragBinding binding;
    private AddSubscriptionViewModel viewmodel;

    public DateFragment() {
        // Required empty public constructor
    }

    public static DateFragment newInstance() {
        DateFragment fragment = new DateFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DateFragBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddSubscriptionActivity.obtainViewModel(getActivity());

        if(viewmodel.startDateCalender != null){
            String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.startDateCalender.getTime());
            binding.startDatePicker.setText(date);
        }else{
            binding.startDatePicker.setText(R.string.start_date_label);
        }

        viewmodel.startDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.startDateCalender.getTime());
                    binding.startDatePicker.setText(date);
                }
            }
        });

        binding.startDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubscriptionsStartDatePickerFragment datePickerFragment = new SubscriptionsStartDatePickerFragment();
                datePickerFragment.show(getFragmentManager(),"datePicker");
            }
        });
    }

    @Override
    public boolean validate() {
        if(viewmodel.startDateCalender != null){
            if (binding.quantity.getText().toString().equalsIgnoreCase(""))
                viewmodel.quantity = "1";
            else
                viewmodel.quantity = binding.quantity.getText().toString().trim();

            return true;
        }else{
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.subscription_start_date_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
