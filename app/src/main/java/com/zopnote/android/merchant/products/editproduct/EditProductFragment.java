package com.zopnote.android.merchant.products.editproduct;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.PricingModeEnum;
import com.zopnote.android.merchant.databinding.EditProductFragBinding;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditProductFragment extends Fragment {
    private EditProductViewModel viewmodel;
    private EditProductFragBinding binding;
    private static final String RADIO_GROUP_PUBLICATION_DAYS_TAG = "radioGroupPublicationDays";

    public EditProductFragment() {
    }

    public static EditProductFragment newInstance() {
        EditProductFragment fragment = new EditProductFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = EditProductFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = EditProductActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                // required to fetch data
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchant.removeObserver(this);
            }
        });

        setupView();
        clearFocus();

        if(viewmodel.addProduct){
            binding.saveProductChangesButton.setText(R.string.button_add_product);
        }else{
            binding.saveProductChangesButton.setText(R.string.save_changes_label);
        }

        binding.saveProductChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject productObject = getAllFields();
                if(productObject != null){
                    if (NetworkUtil.enforceNetworkConnection(getContext())) {
                        viewmodel.updateProduct(productObject);
                    }
                }
            }
        });
    }

    private void setupView() {
        binding.name.setText(viewmodel.product.getName() +" ("+viewmodel.product.getShortCode()+")");
        populateViewValues();
        setupOnClickListeners();
    }

    private void populateViewValues() {
        setupPricingTypeView();
        setupServiceChargeView();
    }

    private void setupServiceChargeView() {
        binding.deliveryChargeLayout.setVisibility(View.VISIBLE);

        if(viewmodel.addProduct){
            binding.deliveryChargeAmountLayout.setVisibility(View.GONE);
            return;
        }

        if(viewmodel.product.getServiceCharge() != null){
            if(viewmodel.product.getServiceCharge() == 0d){
                setupNoDeliveryChargeView();
            }else{
                setupAddDeliveryChargeView();
            }
        }else {
            setupNoDeliveryChargeView();
        }
    }

    private void setupNoDeliveryChargeView() {
        binding.radioBtnNoDeliveryCharge.setChecked(true);
        binding.deliveryChargeAmountLayout.setVisibility(View.GONE);
    }

    private void setupAddDeliveryChargeView() {
        binding.radioBtnAddDeliveryCharge.setChecked(true);
        binding.deliveryChargeAmountLayout.setVisibility(View.VISIBLE);

        if(viewmodel.product.getServiceCharge() != null){
            String formattedDeliveryCharge = FormatUtil.AMOUNT_FORMAT.format(viewmodel.product.getServiceCharge());
            binding.deliveryCharge.setText(formattedDeliveryCharge);
        }
    }

    private void setupPricingTypeView() {
        binding.publicationDaysAndPricingLayout.setVisibility(View.VISIBLE);

        String pricingMode = viewmodel.product.getPricingMode();

        if (PricingModeEnum.DAILY.name().equalsIgnoreCase(pricingMode)) {
            setupDailyPricingView();
        } else if (PricingModeEnum.MONTHLY.name().equalsIgnoreCase(pricingMode)) {
            setupMonthlyPricingView();
        } else if (PricingModeEnum.ISSUE.name().equalsIgnoreCase(pricingMode)) {
            setupPerIssuePricingView();
        }else{
            //show pricing mode view
            showPricingModeView();
        }
    }

    private void showPricingModeView() {
        if("Magazine".equalsIgnoreCase(viewmodel.product.getType())){
            setupPerIssuePricingView();
        }else{
            binding.paperPricingModeSelectionLayout.setVisibility(View.VISIBLE);
            binding.magazinePricingModeSelectionLayout.setVisibility(View.GONE);
            binding.magazineIsPromotedSelectionLayout.setVisibility(View.GONE);

            int pricingModeCheckedRadioButtonId = binding.radioGroupPricingMode.getCheckedRadioButtonId();
            if(pricingModeCheckedRadioButtonId != -1){
                if(pricingModeCheckedRadioButtonId == R.id.radioBtnPaperDailyPrice){
                    setupDailyPricingView();
                }else if(pricingModeCheckedRadioButtonId == R.id.radioBtnPaperMonthlyPrice){
                    setupMonthlyPricingView();
                }
            }else {
                binding.publicationDaysAndPricingLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setupPerIssuePricingView() {
        //magazine, per issue
        binding.paperPricingModeSelectionLayout.setVisibility(View.GONE);
        binding.magazinePricingModeSelectionLayout.setVisibility(View.VISIBLE);

        binding.publicationDaysAndPricingLayout.setVisibility(View.VISIBLE);
        binding.publicationDaysAndPriceContainer.removeAllViews();

        binding.publicationDaysAndPriceLabel.setText(R.string.publication_day);
        final RadioGroup radioGroupPublicationDays = (RadioGroup) LayoutInflater.from(getContext()).inflate(R.layout.add_publication_days, binding.publicationDaysAndPriceContainer, false);
        radioGroupPublicationDays.setTag(RADIO_GROUP_PUBLICATION_DAYS_TAG);

        for (int dayOfWeek= 1; dayOfWeek <= 7 ; dayOfWeek++){
            String day = FormatUtil.getDayOfWeekDisplayName(dayOfWeek);

            RadioButton radioBtnPublicationDay = (RadioButton) radioGroupPublicationDays.getChildAt(dayOfWeek-1);
            radioBtnPublicationDay.setText(day);
            radioBtnPublicationDay.setChecked(false); //default value

            radioBtnPublicationDay.setTag(day);

            if(viewmodel.product.getPublishDay() != null) {
                if(viewmodel.product.getPublishDay().contains(day)){
                    radioBtnPublicationDay.setChecked(true);
                }else {
                    radioBtnPublicationDay.setChecked(false);
                }
            }

            radioBtnPublicationDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RadioButton)radioGroupPublicationDays.getChildAt(0)).setError(null);
                }
            });
        }

        binding.publicationDaysAndPriceContainer.addView(radioGroupPublicationDays);

        Map<String, Double> priceMap = viewmodel.product.getPrice();
        if(priceMap != null && priceMap.containsKey("Issue")){
            //assuming one object present in map with key = "Issue"
            Double price = priceMap.get("Issue");

            String formattedIssuePrice = FormatUtil.AMOUNT_FORMAT.format(price);
            binding.perIssuePrice.setText(formattedIssuePrice);
        }
        binding.radioBtnPaperMonthlyPrice.setChecked(true);

        binding.magazineIsPromotedSelectionLayout.setVisibility(View.VISIBLE);

        if(! viewmodel.addProduct){
            if(viewmodel.product.getIsPromoted()){
                binding.radioBtnPromoted.setChecked(true);
            }else{
                binding.radioBtnNotPromoted.setChecked(true);
            }
        }
    }

    private void setupMonthlyPricingView() {
        //news paper, monthly
        binding.paperPricingModeSelectionLayout.setVisibility(View.VISIBLE);
        binding.magazinePricingModeSelectionLayout.setVisibility(View.GONE);
        binding.magazineIsPromotedSelectionLayout.setVisibility(View.GONE);
        binding.radioBtnPaperMonthlyPrice.setChecked(true);

        if(! viewmodel.addProduct){
            //don't allow mode change for existing publication
            binding.radioBtnPaperDailyPrice.setEnabled(false);
        }

        addMonthlyPriceView();
    }

    private void setupDailyPricingView() {
        //news paper, daily
        binding.paperPricingModeSelectionLayout.setVisibility(View.VISIBLE);
        binding.publicationDaysAndPricingLayout.setVisibility(View.VISIBLE);
        binding.magazinePricingModeSelectionLayout.setVisibility(View.GONE);
        binding.magazineIsPromotedSelectionLayout.setVisibility(View.GONE);
        binding.radioBtnPaperDailyPrice.setChecked(true);

        if(! viewmodel.addProduct){
            //don't allow mode change for existing publication
            binding.radioBtnPaperMonthlyPrice.setEnabled(false);
        }

        addPublicationDaysAndDailyPriceView();
    }

    private void addMonthlyPriceView() {
        binding.publicationDaysAndPricingLayout.setVisibility(View.VISIBLE);
        binding.publicationDaysAndPriceContainer.removeAllViews();

        binding.publicationDaysAndPriceLabel.setText(R.string.fixed_monthly_price_label);
        View priceItemView = LayoutInflater.from(getContext()).inflate(R.layout.add_price_item, binding.publicationDaysAndPriceContainer, false);

        Map<String, Double> priceMap = viewmodel.product.getPrice();
        if(priceMap != null && priceMap.containsKey("Monthly")){
            //assuming one object present in map with key = "Monthly"
            Double price = priceMap.get("Monthly");
            String formattedIssuePrice = FormatUtil.AMOUNT_FORMAT.format(price);
            ((EditText)priceItemView.findViewById(R.id.price)).setText(formattedIssuePrice);
        }

        binding.publicationDaysAndPriceContainer.addView(priceItemView);
    }

    private void addPublicationDaysAndDailyPriceView() {
        binding.publicationDaysAndPriceContainer.removeAllViews();
        binding.publicationDaysAndPriceLabel.setText(R.string.publication_days_and_price_label);

        for (int dayOfWeek= 1; dayOfWeek <= 7 ; dayOfWeek++){
            String day = FormatUtil.getDayOfWeekDisplayName(dayOfWeek);
            final View publicationDayItemView = LayoutInflater.from(getContext()).inflate(R.layout.add_delivery_days_and_price_item, binding.publicationDaysAndPriceContainer, false);
            CheckedTextView dayCheckedTextView = publicationDayItemView.findViewById(R.id.day);

            dayCheckedTextView.setText(day);
            dayCheckedTextView.setChecked(true); //default value

            publicationDayItemView.setTag(day);

            if(viewmodel.product.getPrice() != null){
                if(viewmodel.product.getPrice().containsKey(day)){
                    dayCheckedTextView.setChecked(true);

                    double priceForDay = viewmodel.product.getPrice().get(day);
                    String formattedPriceForDay = FormatUtil.AMOUNT_FORMAT.format(priceForDay);
                    ((EditText)publicationDayItemView.findViewById(R.id.price)).setText(formattedPriceForDay);

                }else {
                    dayCheckedTextView.setChecked(false);
                }
            }

            dayCheckedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckedTextView) v).toggle();
                    ((EditText)publicationDayItemView.findViewById(R.id.price)).setError(null);
                }
            });

            binding.publicationDaysAndPriceContainer.addView(publicationDayItemView);
        }
    }

    private void setupOnClickListeners() {
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

        binding.radioGroupIsPromoted.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnPromoted:
                        binding.radioBtnPromoted.setError(null);
                        break;
                    case R.id.radioBtnNotPromoted:
                        binding.radioBtnPromoted.setError(null);
                        break;
                }
            }
        });
    }


    private void clearFocus() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private JSONObject getAllFields() {
        JSONObject rootJsonObject = new JSONObject();
        JSONObject dataJsonObject = new JSONObject();
        try {
            rootJsonObject.put("merchantId", viewmodel.merchant.getValue().getId()); //mandatory
            rootJsonObject.put("productId", viewmodel.product.getId()); //mandatory

            //pricing mode
            if ("Magazine".equalsIgnoreCase(viewmodel.product.getType())) {
                viewmodel.pricingMode = PricingModeEnum.ISSUE.name();

                dataJsonObject.put("pricingMode", PricingModeEnum.ISSUE.name());
            } else {
                int pricingModeCheckedRadioButtonId = binding.radioGroupPricingMode.getCheckedRadioButtonId();
                if (pricingModeCheckedRadioButtonId == R.id.radioBtnPaperDailyPrice) {
                    viewmodel.pricingMode = PricingModeEnum.DAILY.name();

                    dataJsonObject.put("pricingMode", PricingModeEnum.DAILY.name());

                } else if (pricingModeCheckedRadioButtonId == R.id.radioBtnPaperMonthlyPrice) {
                    viewmodel.pricingMode = PricingModeEnum.MONTHLY.name();

                    dataJsonObject.put("pricingMode", PricingModeEnum.MONTHLY.name());
                } else {
                    //error
                    binding.radioBtnPaperMonthlyPrice.setError(getString(R.string.pricing_mode_not_set_error));
                    requestFocus(binding.paperPricingModeSelectionLayout);
                    return null;
                }
            }

            HashMap<String, Double> customPriceMap = new HashMap<>();

            if ("Magazine".equalsIgnoreCase(viewmodel.product.getType())) {
                //magazine
                if (isValidItemAmount(binding.perIssuePrice)) {
                    viewmodel.perIssuePrice = Double.parseDouble(binding.perIssuePrice.getText().toString());

                    customPriceMap.put("Issue", viewmodel.perIssuePrice);

                } else {
                    requestFocus(binding.magazinePricingModeSelectionLayout);
                    return null;
                }

                String publishDay = getPublishDay();
                if (publishDay == null) {
                    //select publication day
                    RadioGroup radioGroup = binding.publicationDaysAndPriceContainer.findViewWithTag(RADIO_GROUP_PUBLICATION_DAYS_TAG);
                    ((RadioButton)radioGroup.getChildAt(0)).setError(getString(R.string.publication_day_not_selected_error));
                    requestFocus(binding.publicationDaysAndPricingLayout);
                    return null;
                } else {
                    dataJsonObject.put("publishDay", publishDay);
                }

            } else if (viewmodel.pricingMode.equalsIgnoreCase(PricingModeEnum.DAILY.name())) {
                //Newspaper, daily pricing

                ArrayList<String> weekDays = new ArrayList<>();

                for (int i = 0; i < 7; i++) {

                    View publicationDayItemView = binding.publicationDaysAndPriceContainer.getChildAt(i);
                    CheckedTextView dayCheckedTextView = publicationDayItemView.findViewById(R.id.day);

                    if (dayCheckedTextView.isChecked()) {
                        //checked
                        String dayOfWeek = (String) publicationDayItemView.getTag();
                        weekDays.add(dayOfWeek);

                        if (viewmodel.pricingMode.equalsIgnoreCase(PricingModeEnum.DAILY.name())) {
                            EditText priceTextView = publicationDayItemView.findViewById(R.id.price);
                            if (isValidItemAmount(priceTextView)) {

                                Double dailyPrice = Double.parseDouble(priceTextView.getText().toString());
                                customPriceMap.put(dayOfWeek, dailyPrice);
                            } else {
                                priceTextView.setError(getString(R.string.invoice_amount_error_message));
                                requestFocus(binding.publicationDaysAndPriceContainer);
                                return null;
                            }
                        }
                    }
                }

                if (weekDays.size() == 0) {
                    Toast.makeText(getContext(), R.string.select_publication_days_toast, Toast.LENGTH_LONG).show();
                    requestFocus(binding.publicationDaysAndPriceContainer);
                    return null;
                }

            } else if (viewmodel.pricingMode.equalsIgnoreCase(PricingModeEnum.MONTHLY.name())) {
                //Newspaper, monthly pricing
                EditText priceTextView = binding.publicationDaysAndPriceContainer.findViewById(R.id.price);
                if (isValidItemAmount(priceTextView)) {

                    Double monthlyPrice = Double.parseDouble(priceTextView.getText().toString());
                    customPriceMap.put("Monthly", monthlyPrice);
                } else {
                    priceTextView.setError(getString(R.string.invoice_amount_error_message));
                    requestFocus(binding.publicationDaysAndPriceContainer);
                    return null;
                }
            }
            dataJsonObject.put("customPriceMap", getCustomPriceMapJsonObject(customPriceMap));


            //is promoted
            if ("Magazine".equalsIgnoreCase(viewmodel.product.getType())) {
                int radioGroupIsPromotedButtonId = binding.radioGroupIsPromoted.getCheckedRadioButtonId();

                if (radioGroupIsPromotedButtonId == R.id.radioBtnPromoted) {
                    dataJsonObject.put("isPromoted", true);
                } else if (radioGroupIsPromotedButtonId == R.id.radioBtnNotPromoted) {
                    dataJsonObject.put("isPromoted", false);
                } else {
                    //error
                    binding.radioBtnPromoted.setError(getString(R.string.specify_promtotion_type_error));
                    requestFocus(binding.radioGroupIsPromoted);
                    return null;
                }
            }

            //delivery charge
            int deliveryChargeCheckedRadioButtonId = binding.radioGroupDeliveryCharge.getCheckedRadioButtonId();
            if (deliveryChargeCheckedRadioButtonId == R.id.radioBtnAddDeliveryCharge) {
                if (isValidItemAmount(binding.deliveryCharge)) {

                    Double deliveryCharge = Double.parseDouble(binding.deliveryCharge.getText().toString());
                    dataJsonObject.put("serviceCharge", deliveryCharge);

                } else {
                    binding.deliveryCharge.setError(getString(R.string.invoice_amount_error_message));
                    requestFocus(binding.deliveryChargeLayout);
                    return null;
                }

            } else if (deliveryChargeCheckedRadioButtonId == R.id.radioBtnNoDeliveryCharge) {
                dataJsonObject.put("serviceCharge", 0d);
            } else {
                //error
                binding.radioBtnAddDeliveryCharge.setError(getString(R.string.specify_delivery_charge_type_error));
                requestFocus(binding.deliveryChargeLayout);
                return null;
            }

            rootJsonObject.put("productData", dataJsonObject);

        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
        return rootJsonObject;
    }

    private void requestFocus(LinearLayout layout) {
        layout.getParent().requestChildFocus(layout, layout);
    }

    private String getPublishDay() {
        RadioGroup radioGroupPublicationDays = binding.publicationDaysAndPriceContainer.findViewWithTag(RADIO_GROUP_PUBLICATION_DAYS_TAG);

        for (int i = 0; i < 7; i++) {
            RadioButton radioButton = (RadioButton) radioGroupPublicationDays.getChildAt(i);

            if(radioButton.isChecked()){
                //checked
                String dayOfWeek = (String) radioButton.getTag();
                return dayOfWeek;
            }
        }
        return null;
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
