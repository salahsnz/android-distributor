package com.zopnote.android.merchant.addcustomer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.ReviewFragBinding;

/**
 * Created by nmohideen on 26/12/17.
 */

public class ReviewFragment extends Fragment {

    private ReviewFragBinding binding;
    private AddCustomerViewModel viewmodel;

    public ReviewFragment() {
        // Requires empty public constructor
    }

    public static ReviewFragment newInstance() {
        ReviewFragment fragment = new ReviewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ReviewFragBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddCustomerActivity.obtainViewModel(getActivity());

        binding.mobileNumber.setText(viewmodel.mobileNumber);
        binding.name.setText(viewmodel.name);
        binding.doorNumber.setText(viewmodel.doorNumber);

        if(viewmodel.addressLine1NameSelectedValueMap.size() > 0){
            addAddressLine1Views();
        }else{
            binding.addressLine1container.setVisibility(View.GONE);
        }

        binding.addressLine2.setText(viewmodel.addressLine2);
        binding.route.setText(viewmodel.route);

        String newspaperSubscriptionsDisplayString = getNewspaperSubscriptionsDisplayString();
        if( ! newspaperSubscriptionsDisplayString.isEmpty()){
            binding.newspaperSubscriptions.setText(newspaperSubscriptionsDisplayString);
        }else{
            binding.newspaperSubscriptionsLayout.setVisibility(View.GONE);
            binding.newspaperSubscriptionsLayoutDivider.setVisibility(View.GONE);
        }

        String magazineSubscriptionsDisplayString = getMagazineSubscriptionsDisplayString();
        if( ! magazineSubscriptionsDisplayString.isEmpty()){
            binding.magazineSubscriptions.setText(magazineSubscriptionsDisplayString);
        }else{
            binding.magazineSubscriptionsLayout.setVisibility(View.GONE);
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void addAddressLine1Views() {
        for (String key : viewmodel.addressLine1NameSelectedValueMap.keySet()) {
            View addressLineView = LayoutInflater.from(ReviewFragment.this.getActivity()).inflate(R.layout.address_line1_view, null);
            ((TextView) addressLineView.findViewById(R.id.label)).setText(viewmodel.addressLine1NameLabelMap.get(key));
            ((TextView) addressLineView.findViewById(R.id.text)).setText(viewmodel.addressLine1NameSelectedValueMap.get(key));
            binding.addressLine1container.addView(addressLineView);
        }
    }

    private String getNewspaperSubscriptionsDisplayString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : viewmodel.productIdMap.keySet()) {

            if(viewmodel.productIdMap.get(key).getType().equalsIgnoreCase("newspaper")){
                if (stringBuilder.length() > 0) {
                    // add separator
                    stringBuilder.append("\n");
                }
                stringBuilder.append(viewmodel.productIdMap.get(key).getName());
            }
        }
        return stringBuilder.toString();
    }

    private String getMagazineSubscriptionsDisplayString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : viewmodel.productIdMap.keySet()) {

            if(viewmodel.productIdMap.get(key).getType().equalsIgnoreCase("magazine")){
                if (stringBuilder.length() > 0) {
                    // add separator
                    stringBuilder.append("\n");
                }
                stringBuilder.append(viewmodel.productIdMap.get(key).getName());
            }
        }
        return stringBuilder.toString();
    }
}
