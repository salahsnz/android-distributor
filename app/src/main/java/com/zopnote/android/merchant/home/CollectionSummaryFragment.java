package com.zopnote.android.merchant.home;


import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.CollectionSummaryFragBinding;
import com.zopnote.android.merchant.reports.collection.CollectionActivity;
import com.zopnote.android.merchant.reports.collection.PaymentFilterOption;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;

import java.util.Calendar;

public class CollectionSummaryFragment extends Fragment {
    private CollectionSummaryFragBinding binding;
    private HomeViewModel viewmodel;

    public CollectionSummaryFragment() {
        // Required empty public constructor
    }

    public static CollectionSummaryFragment newInstance() {
        CollectionSummaryFragment fragment = new CollectionSummaryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = CollectionSummaryFragBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = HomeActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {

                binding.welcomeUser.setText(getResources().getString(R.string.welcome_user, merchant.getOwnerName()));

                String formattedBilledAmount = getFormattedAmount(merchant.getBilled());
                binding.billedAmount.setText(formattedBilledAmount);

                String formattedCashCollectionAmount = getFormattedAmount(merchant.getPaidCash());
                binding.cashCollectionAmount.setText(formattedCashCollectionAmount);

                String formattedOnlineCollectionAmount = getFormattedAmount(merchant.getPaidOnline());
                binding.onlineCollectionAmount.setText(formattedOnlineCollectionAmount);

                String formattedTotalAccountsReceivable = getFormattedAmount(merchant.getPending());
                binding.pendingAmount.setText(formattedTotalAccountsReceivable);

                if (merchant.getBilled()!=0){
                    float paidCashPercentage = (float) (merchant.getPaidCash()*100/merchant.getBilled());
                    binding.cashCollectionAmountPercentage.setText(String.format("%.1f",paidCashPercentage)+
                            getContext().getResources().getString(R.string.percentage_symbol));

                    float paidOnlinePercentage = (float) (merchant.getPaidOnline()*100/merchant.getBilled());
                    binding.onlineCollectionAmountPercentage.setText(String.format("%.1f",paidOnlinePercentage)+
                            getContext().getResources().getString(R.string.percentage_symbol));

                    float pendingPercentage = (float) (merchant.getPending()*100/merchant.getBilled());
                    binding.pendingAmountPercentage.setText(String.format("%.1f",pendingPercentage)+
                            getContext().getResources().getString(R.string.percentage_symbol));
                }
            }
        });

        binding.billedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport("", PaymentFilterOption.BILLED);
                Analytics.logEvent(Event.NAV_BILLED);
            }
        });

        binding.pendingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport("", PaymentFilterOption.PENDING);
                Analytics.logEvent(Event.NAV_PAYMENT_PENDING);
            }
        });

        binding.onlineCollectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport("", PaymentFilterOption.PAID_ONLINE);
                Analytics.logEvent(Event.NAV_ONLINE_COLLECTION);

            }
        });

        binding.cashCollectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport("", PaymentFilterOption.PAID_CASH);
                Analytics.logEvent(Event.NAV_CASH_COLLECTION);
            }
        });
    }

    private void openCollectionReport(String route, PaymentFilterOption filterOption) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Intent intent = new Intent(getContext(), CollectionActivity.class);
        intent.putExtra(Extras.ROUTE, route);
        intent.putExtra(Extras.FILTER_TYPE, filterOption.name());
        intent.putExtra(Extras.MONTH, month);
        intent.putExtra(Extras.YEAR, year);
        getContext().startActivity(intent);
    }

    private String getFormattedAmount(Double amount) {
        return FormatUtil.getRupeePrefixedAmount(getActivity(),
                amount,
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
    }
}
