package com.zopnote.android.merchant.managesubscription.viewcustomization;


import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.PricingModeEnum;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.databinding.ViewCustomizationFragBinding;
import com.zopnote.android.merchant.databinding.ViewDeliveryDaysAndPricingLayoutBinding;
import com.zopnote.android.merchant.managesubscription.addcustomization.AddCustomizationActivity;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;

import java.util.Map;

public class ViewCustomizationFragment extends Fragment {
    private ViewCustomizationFragBinding binding;
    private ViewCustomizationViewModel viewmodel;

    public ViewCustomizationFragment() {
        // Required empty public constructor
    }

    public static ViewCustomizationFragment newInstance() {
        ViewCustomizationFragment fragment = new ViewCustomizationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= ViewCustomizationFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewmodel = ViewCustomizationActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                // required to fetch data
            }
        });
        viewmodel.subscription.observe(this, new Observer<Subscription>() {
            @Override
            public void onChanged(@Nullable Subscription subscription) {
                setupView(subscription);
            }
        });
    }

    private void setupView(final Subscription subscription) {
        binding.name.setText(subscription.getProduct().getName());

        if(subscription.isAnnualSubscription()){
            setupAnnualSubscriptionView(subscription);
        }else{
            setupRegularSubscriptionView(subscription);
        }

        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddCustomizationActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customerId);
                intent.putExtra(Extras.SUBSCRIPTION, subscription);
                getContext().startActivity(intent);
            }
        });
    }

    private void setupAnnualSubscriptionView(Subscription subscription) {
        binding.viewPostpaidInfoLayout.getRoot().setVisibility(View.GONE);

        binding.viewPrepaidInfoLayout.getRoot().setVisibility(View.VISIBLE);

        binding.viewPrepaidInfoLayout.paymentMode.setText(R.string.annual_subscription_label);
        binding.viewPrepaidInfoLayout.startDate.setText(FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, subscription.getStartDate()));
        binding.viewPrepaidInfoLayout.endDate.setText(FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, subscription.getEndDate()));

    }

    private void setupRegularSubscriptionView(Subscription subscription) {
        binding.viewPrepaidInfoLayout.getRoot().setVisibility(View.GONE);
        binding.viewPostpaidInfoLayout.getRoot().setVisibility(View.VISIBLE);

        if(subscription.getProduct().getType() != null){
            binding.viewPostpaidInfoLayout.type.setText(subscription.getProduct().getType());
        }

        binding.viewPostpaidInfoLayout.paymentMode.setText(R.string.regular_subscription_label);

        String pricingType = getPricingType(subscription.getPricingMode());
        binding.viewPostpaidInfoLayout.pricingType.setText(pricingType);

        setupDeliveryDaysAndPriceView(subscription);

        if(subscription.getServiceCharge() != null){
            binding.viewPostpaidInfoLayout.deliveryChargeLayout.setVisibility(View.VISIBLE);
            if(subscription.getServiceCharge() == 0d){
                binding.viewPostpaidInfoLayout.deliveryCharge.setText(R.string.not_included_label);
            }else{
                String formattedDeliveryCharge = FormatUtil.getRupeePrefixedAmount(getContext(),subscription.getServiceCharge(),
                        FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
                binding.viewPostpaidInfoLayout.deliveryCharge.setText(formattedDeliveryCharge);
            }
        }else {
            binding.viewPostpaidInfoLayout.deliveryChargeLayout.setVisibility(View.GONE);
        }
    }

    private String getPricingType(PricingModeEnum pricingMode) {
        if(PricingModeEnum.DAILY.name().equalsIgnoreCase(pricingMode.name())){
            return getString(R.string.daily_price_label);
        }

        if(PricingModeEnum.MONTHLY.name().equalsIgnoreCase(pricingMode.name())){
            return getString(R.string.fixed_monthly_price_label);
        }

        if(PricingModeEnum.ISSUE.name().equalsIgnoreCase(pricingMode.name())){
            return getString(R.string.per_issue_price_label);
        }
        return "";
    }

    private void setupDeliveryDaysAndPriceView(Subscription subscription) {
        String pricingMode = subscription.getPricingMode().name();

        if(PricingModeEnum.DAILY.name().equalsIgnoreCase(pricingMode)){
            showDailyPriceView(subscription);
            return;
        }

        if(PricingModeEnum.MONTHLY.name().equalsIgnoreCase(pricingMode)){
            showMonthlyPriceView(subscription);
            return;
        }

        if(PricingModeEnum.ISSUE.name().equalsIgnoreCase(pricingMode)){
            showPerIssuePriceView(subscription);
            return;
        }
    }

    private void showDailyPriceView(Subscription subscription) {
        ViewDeliveryDaysAndPricingLayoutBinding layout = binding.viewPostpaidInfoLayout.deliveryDaysAndPricingLayout;
        layout.paperDailyPriceLayout.setVisibility(View.VISIBLE);
        layout.paperFixedPriceLayout.setVisibility(View.GONE);
        layout.magazinePerIssuePriceLayout.setVisibility(View.GONE);

        layout.paperDailyPriceContainer.removeAllViews();

        if(subscription.getWeekDays() != null){
            addWeekDaysAndPriceView(layout.paperDailyPriceContainer, subscription);
        }
    }

    private void addWeekDaysAndPriceView(LinearLayout container, Subscription subscription) {
        for (int i= 0; i < subscription.getWeekDays().size(); i++){
            int dayOfWeek = subscription.getWeekDays().get(i);
            String day = FormatUtil.getDayOfWeekDisplayName(dayOfWeek);

            double priceForDay = getPriceForDay(subscription.getPrice(), day);
            String formattedPriceForDay = FormatUtil.getRupeePrefixedAmount(getContext(), priceForDay,
                    FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);

            TextView textView = getDayAndPriceTextView(day, formattedPriceForDay);
            container.addView(textView);
        }
    }

    private void addWeekDaysView(LinearLayout container, Subscription subscription) {
        for (int i= 0; i < subscription.getWeekDays().size(); i++){
            int dayOfWeek = subscription.getWeekDays().get(i);
            String day = FormatUtil.getDayOfWeekDisplayName(dayOfWeek);

            TextView textView = getDayAndPriceTextView(day, null);
            container.addView(textView);
        }
    }

    private TextView getDayAndPriceTextView(String day, String formattedPriceForDay) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(32, 0, 0, 8);
        textView.setLayoutParams(params);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        textView.setTextColor(getResources().getColor(R.color.text_primary));

        String text;
        if(formattedPriceForDay != null) {
            text = String.format("%s - %s", day, formattedPriceForDay);
        }else{
            text = day;
        }
        textView.setText(text);

        return textView;
    }

    private double getPriceForDay(Map<String, Double> price, String day) {
        if(price.containsKey(day)){
            return price.get(day);
        }
        return 0d;
    }

    private void showMonthlyPriceView(Subscription subscription) {
        ViewDeliveryDaysAndPricingLayoutBinding layout = binding.viewPostpaidInfoLayout.deliveryDaysAndPricingLayout;
        layout.paperDailyPriceLayout.setVisibility(View.GONE);
        layout.paperFixedPriceLayout.setVisibility(View.VISIBLE);
        layout.magazinePerIssuePriceLayout.setVisibility(View.GONE);


        Double price = 0d;
        Map<String, Double> priceMap = subscription.getPrice();
        if(priceMap != null && priceMap.containsKey("Monthly")){
            //assuming one object present in map with key = "Monthly"
            price = priceMap.get("Monthly");
        }

        layout.deliveryDaysContainer.removeAllViews();

        if(subscription.getWeekDays() != null){
            addWeekDaysView(layout.deliveryDaysContainer, subscription);
        }

        String formattedIssuePrice = FormatUtil.getRupeePrefixedAmount(getContext(), price,
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        layout.fixedMonthlyPrice.setText(formattedIssuePrice);
    }

    private void showPerIssuePriceView(Subscription subscription) {
        ViewDeliveryDaysAndPricingLayoutBinding layout = binding.viewPostpaidInfoLayout.deliveryDaysAndPricingLayout;
        layout.paperDailyPriceLayout.setVisibility(View.GONE);
        layout.paperFixedPriceLayout.setVisibility(View.GONE);
        layout.magazinePerIssuePriceLayout.setVisibility(View.VISIBLE);

        Double price = 0d;
        Map<String, Double> priceMap = subscription.getPrice();
        if(priceMap != null && priceMap.containsKey("Issue")){
            //assuming one object present in map with key = "Issue"
            price = priceMap.get("Issue");
        }

        String formattedIssuePrice = FormatUtil.getRupeePrefixedAmount(getContext(), price,
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        layout.perIssuePrice.setText(formattedIssuePrice);
    }
}
