package com.zopnote.android.merchant.addcustomer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.LocationFragBinding;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.Validatable;

/**
 * Created by nmohideen on 26/12/17.
 */

public class LocationFragment extends Fragment implements Validatable {

    private LocationFragBinding binding;
    private AddCustomerViewModel viewmodel;

    public LocationFragment() {
        // Requires empty public constructor
    }

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = LocationFragBinding.inflate(inflater, container, false);

        // TODO: Update after API is wiring is complete
//        final String lastUsedAddressLine1 = Prefs.getString(AppConstants.PREFS_LAST_USED_ADDRESS_LINE1, null);
//        final String lastUsedAddressLine2 = Prefs.getString(AppConstants.PREFS_LAST_USED_ADDRESS_LINE2, null);
        final String lastUsedAddressLine1 = Prefs.getString(AppConstants.PREFS_LAST_USED_ADDRESS_LINE1, "I Floor, Block B");
        final String lastUsedAddressLine2 = Prefs.getString(AppConstants.PREFS_LAST_USED_ADDRESS_LINE2, "Mantri Square Apartment");

        if (lastUsedAddressLine1 != null
                || lastUsedAddressLine2 != null) {

            if (lastUsedAddressLine1 != null) {
                binding.lastUsedAddressLine1.setText(lastUsedAddressLine1);
                binding.lastUsedAddressLine1.setVisibility(View.VISIBLE);
            } else {
                binding.lastUsedAddressLine1.setVisibility(View.GONE);
            }

            if (lastUsedAddressLine2 != null) {
                binding.lastUsedAddressLine2.setText(lastUsedAddressLine2);
                binding.lastUsedAddressLine2.setVisibility(View.VISIBLE);
            } else {
                binding.lastUsedAddressLine2.setVisibility(View.GONE);
            }

            binding.lastUsedLocationLayout.setVisibility(View.VISIBLE);
        } else {
            binding.lastUsedLocationLayout.setVisibility(View.GONE);
        }

        binding.selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // copy
                binding.addressLine1.setText(lastUsedAddressLine1);
                binding.addressLine2.setText(lastUsedAddressLine2);

                // next
                ((AddCustomerActivity) getActivity()).nextFragment();
            }
        });

        binding.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.addressLine1.setText(lastUsedAddressLine1);
                binding.addressLine2.setText(lastUsedAddressLine2);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddCustomerActivity.obtainViewModel(getActivity());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public boolean validate() {
        if (binding.addressLine2.getText().toString().trim().length() > 0) {
            return true;
        } else {
            binding.addressLine2.setError(getActivity().getResources().getString(R.string.addressline2_error_message));
            binding.addressLine2.requestFocus();
            return false;
        }
    }
}
