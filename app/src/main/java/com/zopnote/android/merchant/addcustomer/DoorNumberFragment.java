package com.zopnote.android.merchant.addcustomer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.DoorNumberFragBinding;
import com.zopnote.android.merchant.util.Validatable;

/**
 * Created by nmohideen on 26/12/17.
 */

public class DoorNumberFragment extends Fragment implements Validatable {

    private DoorNumberFragBinding binding;
    private AddCustomerViewModel viewmodel;

    public DoorNumberFragment() {
        // Requires empty public constructor
    }

    public static DoorNumberFragment newInstance() {
        DoorNumberFragment fragment = new DoorNumberFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DoorNumberFragBinding.inflate(inflater, container, false);

        binding.doorNumber.getEditableText().setFilters(
                new InputFilter[]{
                        new InputFilter.AllCaps()
                });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddCustomerActivity.obtainViewModel(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();

        viewmodel.doorNumber = binding.doorNumber.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.doorNumber.setText(viewmodel.doorNumber);

        if (binding.doorNumber.getText().toString().trim().length() == 0) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            InputMethodManager inputMethodManager = (InputMethodManager)getActivity(). getSystemService(Activity.INPUT_METHOD_SERVICE);

            binding.doorNumber.requestFocus();
            inputMethodManager.showSoftInput(binding.doorNumber, 0);
        }
    }

    @Override
    public boolean validate() {
        if (binding.doorNumber.getText().toString().trim().length() > 0) {
            return true;
        } else {
            binding.doorNumber.setError(getActivity().getResources().getString(R.string.door_number_error_message));
            binding.doorNumber.requestFocus();
            return false;
        }
    }
}
