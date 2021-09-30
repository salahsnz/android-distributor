package com.zopnote.android.merchant.viewcustomer;


import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.CheckOutDateFragBinding;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.Utils;

public class CheckoutDateFragment extends Fragment {
    private CheckOutDateFragBinding binding;
    private ViewCustomerViewModel viewmodel;
    private ProgressDialog progressDialog;
    public CheckoutDateFragment() {
        // Required empty public constructor
    }

    public static CheckoutDateFragment newInstance() {
        CheckoutDateFragment fragment = new CheckoutDateFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = CheckOutDateFragBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = ViewCustomerActivity.obtainViewModel(getActivity());

        if(viewmodel.checkOutDateCalender != null){
            String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.checkOutDateCalender.getTime());
            binding.startDatePicker.setText(date);
        }else{
            binding.startDatePicker.setText(R.string.check_out_date_label);
        }

        viewmodel.checkOutDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.checkOutDateCalender.getTime());
                    binding.startDatePicker.setText(date);
                }
            }
        });

        binding.startDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOutDatePickerFragment datePickerFragment = new CheckOutDatePickerFragment();
                datePickerFragment.show(getFragmentManager(),"datePicker");
            }
        });

        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! Utils.enforceConnection(getActivity())) {
                    return ;
                }

                if (validate())
                    viewmodel.checkOut();
            }
        });


        viewmodel.checkoutApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage(getActivity().getResources().getString(R.string.checkout_customer_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.checkoutApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.checkoutApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            getActivity().getResources().getString(R.string.checkout_customer_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.checkoutApiCallSuccess.setValue(false);
                    if(getActivity() != null){
                        getActivity().finish();
                    }
                }
            }
        });

        viewmodel.checkoutApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.checkoutApiCallError.setValue(false);
                }
            }
        });
    }


    public boolean validate() {
        if(viewmodel.checkOutDateCalender != null){
            return true;
        }else{
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.checkout_date_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
