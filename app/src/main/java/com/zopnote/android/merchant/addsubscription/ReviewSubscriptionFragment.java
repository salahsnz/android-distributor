package com.zopnote.android.merchant.addsubscription;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.zopnote.android.merchant.databinding.ReviewSubscriptionFragBinding;
import com.zopnote.android.merchant.util.FormatUtil;

public class ReviewSubscriptionFragment extends Fragment {

    private ReviewSubscriptionFragBinding binding;
    private AddSubscriptionViewModel viewmodel;

    public ReviewSubscriptionFragment() {
        // Requires empty public constructor
    }

    public static ReviewSubscriptionFragment newInstance() {
        ReviewSubscriptionFragment fragment = new ReviewSubscriptionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ReviewSubscriptionFragBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddSubscriptionActivity.obtainViewModel(getActivity());

        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, viewmodel.startDateCalender.getTime());
        binding.startDate.setText(date);
        binding.quantity.setText(viewmodel.quantity);

        String newspaperSubscriptionsDisplayString = getNewspaperSubscriptionsDisplayString();
        if( ! newspaperSubscriptionsDisplayString.isEmpty()){
            binding.newspaperSubscriptions.setText(newspaperSubscriptionsDisplayString);
        }else{
            binding.newspaperSubscriptionsLayout.setVisibility(View.GONE);
        }

        String magazineSubscriptionsDisplayString = getMagazineSubscriptionsDisplayString();
        if( ! magazineSubscriptionsDisplayString.isEmpty()){
            binding.magazineSubscriptions.setText(magazineSubscriptionsDisplayString);
        }else{
            binding.magazineSubscriptionsLayout.setVisibility(View.GONE);
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
