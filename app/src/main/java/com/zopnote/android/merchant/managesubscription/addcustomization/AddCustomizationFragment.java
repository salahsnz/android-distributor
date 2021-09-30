package com.zopnote.android.merchant.managesubscription.addcustomization;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.PricingModeEnum;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.databinding.AddCustomizationFragBinding;
import com.zopnote.android.merchant.managesubscription.editsubscription.EditSubscriptionDatePickerFragment;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCustomizationFragment extends Fragment {
    private AddCustomizationViewModel viewmodel;
    private AddCustomizationFragBinding binding;
    private AnnualSubscriptionDatePickerFragment datePickerFragment;
    private Subscription subscription;
    private boolean isCustomized;

    public AddCustomizationFragment() {
    }

    public static AddCustomizationFragment newInstance() {
        AddCustomizationFragment fragment = new AddCustomizationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AddCustomizationFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewmodel = AddCustomizationActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                // required to fetch data
            }
        });

        viewmodel.subscription.observe(this, new Observer<Subscription>() {
            @Override
            public void onChanged(@Nullable Subscription subs) {
                subscription = subs;
                setupView();
                viewmodel.subscription.removeObserver(this);

                clearFocus();
            }
        });

        binding.saveSubscriptionChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject customPricingObject = getAllFields();
                if(customPricingObject != null){
                    if (NetworkUtil.enforceNetworkConnection(getContext())) {
                        viewmodel.customizeSubscription(customPricingObject);
                    }
                }
            }
        });
    }

    private boolean isValidDateRange() {

        if (viewmodel.annualSubscriptionStartDateCalender != null && viewmodel.annualSubscriptionEndDateCalender != null) {
            if (viewmodel.annualSubscriptionEndDateCalender.getTime().compareTo(viewmodel.annualSubscriptionStartDateCalender.getTime()) >= 0) {
                return true;
            } else
                return false;
        } else {
            return false;
        }
    }

    private Date getStartDate() {
        if(viewmodel.annualSubscriptionStartDateChanged.getValue()){
            return viewmodel.annualSubscriptionStartDateCalender.getTime();
        }else{
            return subscription.getStartDate();
        }
    }

    private Date getEndDate() {
        if(viewmodel.annualSubscriptionEndDateChanged.getValue()){
            return viewmodel.annualSubscriptionEndDateCalender.getTime();
        }else {
            return subscription.getEndDate();
        }
    }

    private void setupView() {
        binding.name.setText(subscription.getProduct().getName());

        boolean isCustomized = isSubscriptionCustomized(subscription);
        if(isCustomized){
            this.isCustomized = true;
            populateViewValues();
        }else{
            this.isCustomized = false;
            hideAllViewsExceptSubscriptionTypeView();
        }

        setupOnClickListeners();
    }

    private void hideAllViewsExceptSubscriptionTypeView() {
        binding.annualSubscriptionDateSelectLayout.setVisibility(View.GONE);

        binding.paperPricingModeSelectionLayout.setVisibility(View.GONE);
        binding.magazinePricingModeSelectionLayout.setVisibility(View.GONE);
        binding.deliveryDaysAndPricingLayout.setVisibility(View.GONE);
        binding.deliveryChargeLayout.setVisibility(View.GONE);
        binding.saveSubscriptionChangesButton.setVisibility(View.GONE);
    }

    private void setupOnClickListeners() {
        binding.radioGroupPaymentMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnAnnualSubscription:
                        binding.radioBtnRegularSubscription.setError(null);

                        setupAnnualSubscriptionTypeView();
                        if( !isCustomized){
                            binding.saveSubscriptionChangesButton.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.radioBtnRegularSubscription:
                        binding.radioBtnRegularSubscription.setError(null);
                        if( !isCustomized){
                            binding.saveSubscriptionChangesButton.setVisibility(View.GONE);
                        }

                        setupRegularBillingTypeView();
                        setupPricingTypeView();
                        setupServiceChargeView();

                        break;
                }
            }
        });

        binding.radioGroupPricingMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnPaperDailyPrice:
                        binding.radioBtnPaperMonthlyPrice.setError(null);

                        setupDailyPricingView();
                        setupServiceChargeView();
                        break;
                    case R.id.radioBtnPaperMonthlyPrice:
                        binding.radioBtnPaperMonthlyPrice.setError(null);

                        setupMonthlyPricingView();
                        setupServiceChargeView();
                        break;
                }
            }
        });

        binding.radioGroupDeliveryCharge.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnAddDeliveryCharge:
                        binding.radioBtnAddDeliveryCharge.setError(null);

                        setupAddDeliveryChargeView();
                        break;
                    case R.id.radioBtnNoDeliveryCharge:
                        binding.radioBtnAddDeliveryCharge.setError(null);

                        setupNoDeliveryChargeView();
                        break;
                }
            }
        });
    }

    private void populateViewValues() {
        if(subscription.isAnnualSubscription()){
            setupAnnualSubscriptionTypeView();
        }else{
            setupRegularBillingTypeView();
            setupPricingTypeView();
            setupServiceChargeView();
        }
    }

    private void setupAnnualSubscriptionTypeView() {
        binding.radioBtnAnnualSubscription.setChecked(true);
        binding.annualSubscriptionDateSelectLayout.setVisibility(View.VISIBLE);

        if(subscription.getStartDate() != null){
            String annualSubscriptionStartDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, subscription.getStartDate());
            binding.startDatePicker.setText(annualSubscriptionStartDate);

            if(viewmodel.annualSubscriptionStartDateCalender == null){
                //init annual subscription startDate calender
                viewmodel.annualSubscriptionStartDateCalender = FormatUtil.getLocalCalender();
            }

            viewmodel.annualSubscriptionStartDateCalender.setTime(subscription.getStartDate());
        }

        if(subscription.getEndDate() != null){
            String annualSubscriptionEndDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, subscription.getEndDate());
            binding.endDatePicker.setText(annualSubscriptionEndDate);

            if( viewmodel.annualSubscriptionEndDateCalender == null){
                //init endDate calender
                viewmodel.annualSubscriptionEndDateCalender = FormatUtil.getLocalCalender();
            }

            viewmodel.annualSubscriptionEndDateCalender.setTime(subscription.getEndDate());
        }

        datePickerFragment = (AnnualSubscriptionDatePickerFragment) getActivity().getSupportFragmentManager().findFragmentByTag("datePicker");

        if(datePickerFragment == null){
            datePickerFragment = new AnnualSubscriptionDatePickerFragment();
        }

        binding.startDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.setFlag(EditSubscriptionDatePickerFragment.FLAG_START_DATE);
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        binding.endDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.setFlag(EditSubscriptionDatePickerFragment.FLAG_END_DATE);
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        viewmodel.annualSubscriptionStartDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if (dateChanged) {
                    setStartDate();
                }
            }
        });

        viewmodel.annualSubscriptionEndDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if (dateChanged) {
                    if (viewmodel.annualSubscriptionEndDateCalender != null) {
                        setEndDate();
                    }
                }
            }
        });

        binding.paperPricingModeSelectionLayout.setVisibility(View.GONE);
        binding.magazinePricingModeSelectionLayout.setVisibility(View.GONE);
        binding.deliveryDaysAndPricingLayout.setVisibility(View.GONE);
        binding.deliveryChargeLayout.setVisibility(View.GONE);
    }

    private void setStartDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.annualSubscriptionStartDateCalender.getTime());
        binding.startDatePicker.setText(date);
    }

    private void setEndDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.annualSubscriptionEndDateCalender.getTime());
        binding.endDatePicker.setText(date);
    }

    private void setupRegularBillingTypeView() {
        binding.annualSubscriptionDateSelectLayout.setVisibility(View.GONE);

        if(isCustomized){
            binding.radioBtnRegularSubscription.setChecked(true);
        }
    }

    private void setupPricingTypeView() {
        binding.deliveryDaysAndPricingLayout.setVisibility(View.VISIBLE);

        if(subscription.getPricingMode() != null) {
            PricingModeEnum pricingMode = subscription.getPricingMode();

            if (PricingModeEnum.DAILY.name().equalsIgnoreCase(pricingMode.name())) {
                setupDailyPricingView();
            } else if (PricingModeEnum.MONTHLY.name().equalsIgnoreCase(pricingMode.name())) {
                setupMonthlyPricingView();
            } else if (PricingModeEnum.ISSUE.name().equalsIgnoreCase(pricingMode.name())) {
                setupPerIssuePricingView();
            }
        }else{
            //show pricing mode view
            showPricingModeView();
        }
    }

    private void showPricingModeView() {
        if("Magazine".equalsIgnoreCase(subscription.getProduct().getType())){
            setupPerIssuePricingView();
        }else{
            binding.paperPricingModeSelectionLayout.setVisibility(View.VISIBLE);
            binding.magazinePricingModeSelectionLayout.setVisibility(View.GONE);

            int pricingModeCheckedRadioButtonId = binding.radioGroupPricingMode.getCheckedRadioButtonId();
            if(pricingModeCheckedRadioButtonId != -1){
                if(pricingModeCheckedRadioButtonId == R.id.radioBtnPaperDailyPrice){
                    setupDailyPricingView();
                }else if(pricingModeCheckedRadioButtonId == R.id.radioBtnPaperMonthlyPrice){
                    setupMonthlyPricingView();
                }
            }
        }
    }

    private void setupPerIssuePricingView() {
        //magazine, per issue
        binding.paperPricingModeSelectionLayout.setVisibility(View.GONE);
        binding.magazinePricingModeSelectionLayout.setVisibility(View.VISIBLE);
        binding.deliveryDaysAndPricingLayout.setVisibility(View.GONE);

        Map<String, Double> priceMap = subscription.getPrice();
        if(priceMap != null && priceMap.containsKey("Issue")){
            //assuming one object present in map with key = "Issue"
            Double price = priceMap.get("Issue");

            String formattedIssuePrice = FormatUtil.AMOUNT_FORMAT.format(price);
            binding.perIssuePrice.setText(formattedIssuePrice);
        }
    }

    private void setupMonthlyPricingView() {
        //news paper, monthly
        binding.paperPricingModeSelectionLayout.setVisibility(View.VISIBLE);
        binding.magazinePricingModeSelectionLayout.setVisibility(View.GONE);
        if(isCustomized){
            binding.radioBtnPaperMonthlyPrice.setChecked(true);
        }

        addDeliveryDaysAndMonthlyPriceView();
    }

    private void setupDailyPricingView() {
        //news paper, daily
        binding.paperPricingModeSelectionLayout.setVisibility(View.VISIBLE);
        binding.magazinePricingModeSelectionLayout.setVisibility(View.GONE);
        if(isCustomized){
            binding.radioBtnPaperDailyPrice.setChecked(true);
        }
        addDeliveryDaysAndDailyPriceView();
    }

    private void addDeliveryDaysAndMonthlyPriceView() {
        binding.deliveryDaysAndPriceContainer.removeAllViews();

        for (int dayOfWeek= 1; dayOfWeek <= 7 ; dayOfWeek++){
            String day = FormatUtil.getDayOfWeekDisplayName(dayOfWeek);

            View deliveryDayItemView = LayoutInflater.from(getContext()).inflate(R.layout.add_delivery_days_item, binding.deliveryDaysAndPriceContainer, false);
            CheckedTextView dayCheckedTextView = deliveryDayItemView.findViewById(R.id.day);

            dayCheckedTextView.setText(day);
            dayCheckedTextView.setChecked(true); //default value

            deliveryDayItemView.setTag(dayOfWeek);

            if(subscription.getWeekDays() != null) {
                if(subscription.getWeekDays().contains(dayOfWeek)){
                    dayCheckedTextView.setChecked(true);
                }else {
                    dayCheckedTextView.setChecked(false);
                }
            }

            dayCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckedTextView) v).toggle();
                }
            });

            binding.deliveryDaysAndPriceContainer.addView(deliveryDayItemView);
        }

        View priceItemView = LayoutInflater.from(getContext()).inflate(R.layout.add_price_item, binding.deliveryDaysAndPriceContainer, false);

        Map<String, Double> priceMap = subscription.getPrice();
        if(priceMap != null && priceMap.containsKey("Monthly")){
            //assuming one object present in map with key = "Monthly"
            Double price = priceMap.get("Monthly");
            String formattedIssuePrice = FormatUtil.AMOUNT_FORMAT.format(price);
            ((EditText)priceItemView.findViewById(R.id.price)).setText(formattedIssuePrice);
        }

        binding.deliveryDaysAndPriceContainer.addView(priceItemView);
    }

    private void addDeliveryDaysAndDailyPriceView() {
        binding.deliveryDaysAndPriceContainer.removeAllViews();

        for (int dayOfWeek= 1; dayOfWeek <= 7 ; dayOfWeek++){
            String day = FormatUtil.getDayOfWeekDisplayName(dayOfWeek);
            final View deliveryDayItemView = LayoutInflater.from(getContext()).inflate(R.layout.add_delivery_days_and_price_item, binding.deliveryDaysAndPriceContainer, false);
            CheckedTextView dayCheckedTextView = deliveryDayItemView.findViewById(R.id.day);

            dayCheckedTextView.setText(day);
            dayCheckedTextView.setChecked(true); //default value

            deliveryDayItemView.setTag(dayOfWeek);

            if(subscription.getWeekDays() != null){
                if(subscription.getWeekDays().contains(dayOfWeek)){
                    dayCheckedTextView.setChecked(true);

                    if(subscription.getPrice().containsKey(day)){
                        double priceForDay = subscription.getPrice().get(day);
                        String formattedPriceForDay = FormatUtil.AMOUNT_FORMAT.format(priceForDay);
                        ((EditText)deliveryDayItemView.findViewById(R.id.price)).setText(formattedPriceForDay);
                    }
                }else {
                    dayCheckedTextView.setChecked(false);
                }
            }

            dayCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckedTextView) v).toggle();
                    ((EditText)deliveryDayItemView.findViewById(R.id.price)).setError(null);
                }
            });

            binding.deliveryDaysAndPriceContainer.addView(deliveryDayItemView);
        }
    }

    private boolean isSubscriptionCustomized(Subscription subscription) {
        if(subscription.getPricingMode() != null){
            return true;
        }

        if(subscription.isAnnualSubscription()){
            return true;
        }

        return false;
    }

    private void setupServiceChargeView() {
        binding.deliveryChargeLayout.setVisibility(View.VISIBLE);
        if(subscription.getServiceCharge() != null){
            if(subscription.getServiceCharge() == 0d){
                setupNoDeliveryChargeView();
            }else{
                setupAddDeliveryChargeView();
            }
        }else {
            //don't set checked
            int deliveryChargeCheckedRadioButtonId = binding.radioGroupDeliveryCharge.getCheckedRadioButtonId();
            if(deliveryChargeCheckedRadioButtonId != -1){
                if(deliveryChargeCheckedRadioButtonId == R.id.radioBtnAddDeliveryCharge){
                    setupAddDeliveryChargeView();
                }else{
                    setupNoDeliveryChargeView();
                }
            }else {
                binding.radioGroupDeliveryCharge.clearCheck();
                binding.deliveryChargeAmountLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setupNoDeliveryChargeView() {
        binding.radioBtnNoDeliveryCharge.setChecked(true);
        binding.deliveryChargeAmountLayout.setVisibility(View.GONE);
        binding.saveSubscriptionChangesButton.setVisibility(View.VISIBLE);
    }

    private void setupAddDeliveryChargeView() {
        binding.radioBtnAddDeliveryCharge.setChecked(true);
        binding.deliveryChargeAmountLayout.setVisibility(View.VISIBLE);

        if(subscription.getServiceCharge() != null){
            String formattedDeliveryCharge = FormatUtil.AMOUNT_FORMAT.format(subscription.getServiceCharge());
            binding.deliveryCharge.setText(formattedDeliveryCharge);
        }
        binding.saveSubscriptionChangesButton.setVisibility(View.VISIBLE);
    }

    private void clearFocus() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private JSONObject getAllFields() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("subscriptionId", subscription.getId()); //mandatory

            int paymentModeCheckedRadioButtonId = binding.radioGroupPaymentMode.getCheckedRadioButtonId();
            if(paymentModeCheckedRadioButtonId == R.id.radioBtnAnnualSubscription){
                viewmodel.annualSubscription = true;

                jsonObject.put("annualSubscription", true);

            }else if(paymentModeCheckedRadioButtonId == R.id.radioBtnRegularSubscription){
                viewmodel.annualSubscription = false;

                jsonObject.put("annualSubscription", false);

            }else{
                //error
                binding.radioBtnRegularSubscription.setError("Payment mode not set");
                return null;
            }

            if(viewmodel.annualSubscription){
                if ( isValidDateRange()) {
                    jsonObject.put("annualSubscriptionStartDate", getStartDate().getTime());
                    jsonObject.put("annualSubscriptionEndDate", getEndDate().getTime());
                    return jsonObject;
                }else {
                    Toast.makeText(getContext(), R.string.date_range_error_toast, Toast.LENGTH_LONG).show();
                    return null;
                }
            }

            //fields of regular subscription
            //pricing mode
            if("Magazine".equalsIgnoreCase(subscription.getProduct().getType())){
                viewmodel.pricingMode = PricingModeEnum.ISSUE.name();

                jsonObject.put("pricingMode", PricingModeEnum.ISSUE.name());
            }else{
                int pricingModeCheckedRadioButtonId = binding.radioGroupPricingMode.getCheckedRadioButtonId();
                if(pricingModeCheckedRadioButtonId == R.id.radioBtnPaperDailyPrice){
                    viewmodel.pricingMode = PricingModeEnum.DAILY.name();

                    jsonObject.put("pricingMode", PricingModeEnum.DAILY.name());

                }else if(pricingModeCheckedRadioButtonId == R.id.radioBtnPaperMonthlyPrice){
                    viewmodel.pricingMode = PricingModeEnum.MONTHLY.name();

                    jsonObject.put("pricingMode", PricingModeEnum.MONTHLY.name());
                }else {
                    //error
                    binding.radioBtnPaperMonthlyPrice.setError(getString(R.string.pricing_mode_not_set_error));
                    return null;
                }
            }

            HashMap<String, Double> customPriceMap = new HashMap<>();

            if("Magazine".equalsIgnoreCase(subscription.getProduct().getType())){
                //magazine
                if(isValidItemAmount(binding.perIssuePrice)){
                    viewmodel.perIssuePrice = Double.parseDouble(binding.perIssuePrice.getText().toString());

                    customPriceMap.put("Issue", viewmodel.perIssuePrice);

                }else {
                    return null;
                }
            }else{
                ArrayList<Integer> weekDays = new ArrayList<>();

                //newspaper
                // daily pricing/monthly pricing
                for (int i = 0; i < 7; i++) {

                    View deliveryDayItemView =  binding.deliveryDaysAndPriceContainer.getChildAt(i);
                    CheckedTextView dayCheckedTextView = deliveryDayItemView.findViewById(R.id.day);

                    if(dayCheckedTextView.isChecked()){
                        //checked
                        int dayOfWeek = (int) deliveryDayItemView.getTag();
                        weekDays.add(dayOfWeek);

                        if(viewmodel.pricingMode.equalsIgnoreCase(PricingModeEnum.DAILY.name())){
                            EditText priceTextView = deliveryDayItemView.findViewById(R.id.price);
                            if(isValidItemAmount(priceTextView)){

                                Double dailyPrice = Double.parseDouble(priceTextView.getText().toString());
                                String day = FormatUtil.getDayOfWeekDisplayName(dayOfWeek);

                                customPriceMap.put(day, dailyPrice);
                            }else {
                                priceTextView.setError(getString(R.string.invoice_amount_error_message));
                                return null;
                            }
                        }
                    }
                }

                if(viewmodel.pricingMode.equalsIgnoreCase(PricingModeEnum.MONTHLY.name())){
                    EditText priceTextView =  binding.deliveryDaysAndPriceContainer.findViewById(R.id.price);
                    if(isValidItemAmount(priceTextView)){

                        Double monthlyPrice = Double.parseDouble(priceTextView.getText().toString());
                        customPriceMap.put("Monthly", monthlyPrice);
                    }else {
                        priceTextView.setError(getString(R.string.invoice_amount_error_message));
                        return null;
                    }
                }

                if(weekDays.size() == 0){
                    Toast.makeText(getContext(), R.string.select_delivery_days_toast, Toast.LENGTH_SHORT).show();
                    return null;
                }

                jsonObject.put("weekDays", getWeekDaysJsonArray(weekDays));
            }

            jsonObject.put("customPriceMap", getCustomPriceMapJsonObject(customPriceMap));

            //delivery charge
            int deliveryChargeCheckedRadioButtonId = binding.radioGroupDeliveryCharge.getCheckedRadioButtonId();
            if(deliveryChargeCheckedRadioButtonId == R.id.radioBtnAddDeliveryCharge){
                if(isValidItemAmount(binding.deliveryCharge)){

                    Double deliveryCharge = Double.parseDouble(binding.deliveryCharge.getText().toString());
                    jsonObject.put("serviceCharge", deliveryCharge);

                }else {
                    binding.deliveryCharge.setError(getString(R.string.invoice_amount_error_message));
                    return null;
                }


            }else if(deliveryChargeCheckedRadioButtonId == R.id.radioBtnNoDeliveryCharge){

                jsonObject.put("serviceCharge", 0d);

            }else{
                //error
                binding.radioBtnAddDeliveryCharge.setError(getString(R.string.specify_delivery_charge_type_error));
                return null;
            }

        }catch (JSONException ex){
            ex.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    private JSONArray getWeekDaysJsonArray(ArrayList<Integer> weekDays) {
        JSONArray jsonArray = new JSONArray();
        for (int weekDay:weekDays) {
            jsonArray.put(weekDay);
        }
        return jsonArray;
    }

    private JSONObject getCustomPriceMapJsonObject(HashMap<String, Double> customPriceMap) {
        if(customPriceMap.isEmpty()){
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            for (String key : customPriceMap.keySet()) {
                jsonObject.put(key, customPriceMap.get(key));
            }
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isValidItemAmount(EditText amountEditText){
        if( ! amountEditText.getText().toString().trim().isEmpty()){
            try {
                Double.parseDouble(amountEditText.getText().toString());
                return true;
            }catch (Exception ex){
                amountEditText.setError(getString(R.string.invoice_amount_error_message));
                return false;
            }
        }else{
            amountEditText.setError(getString(R.string.invoice_amount_error_message));
            return false;
        }
    }
}
