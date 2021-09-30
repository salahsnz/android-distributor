package com.zopnote.android.merchant.invoice;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.dailyindent.DailyIndentActivity;
import com.zopnote.android.merchant.dailyindent.DailyIndentAdapter;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.DateWiseBills;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.InvoiceItem;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.InvoiceFragBinding;
import com.zopnote.android.merchant.reports.ordersummarycustomerdetails.OrderSummaryDatePickerFragment;
import com.zopnote.android.merchant.updateinvoice.UpdateInvoiceActivity;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.VolleyManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by nmohideen on 26/12/17.
 */

public class InvoiceFragment extends Fragment {

    private InvoiceFragBinding binding;
    private InvoiceViewModel viewmodel;
    private DateWiseInvoiceItemAdapter adapter;
    private RadioButton radioButton;
    private RadioGroup rg1,rg2;
    private InvoiceDatePickerFragment invoiceDatePickerFragment;
    public InvoiceFragment() {
        // Requires empty public constructor
    }

    public static InvoiceFragment newInstance() {
        InvoiceFragment fragment = new InvoiceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = InvoiceFragBinding.inflate(inflater, container, false);

        binding.recyclerView.setHasFixedSize(true);

        adapter = new DateWiseInvoiceItemAdapter(getActivity());
        adapter.setViewModel(DailyIndentActivity.obtainViewModel(this.getActivity()));

        RecyclerView.ItemDecoration dividerDecoration2 = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerDecoration2);
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setAdapter(adapter);

        // hide all
        binding.contentView.setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);


        invoiceDatePickerFragment = (InvoiceDatePickerFragment) getFragmentManager().findFragmentByTag("invoiceDatePicker");
        if(invoiceDatePickerFragment == null){
            invoiceDatePickerFragment = new InvoiceDatePickerFragment();
        }

        binding.startDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CSD","START CLICKED");
                invoiceDatePickerFragment.setFlag(OrderSummaryDatePickerFragment.FLAG_START_DATE);
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
                try {
                    Date sd=ft.parse("2020-01-01");
                    invoiceDatePickerFragment.setStartDate(sd);
                    invoiceDatePickerFragment.setEndDate(new Date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                invoiceDatePickerFragment.show(getActivity().getSupportFragmentManager(), "invoiceDatePicker");
            }
        });

        binding.endDatePickerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invoiceDatePickerFragment.setFlag(OrderSummaryDatePickerFragment.FLAG_END_DATE);
                Log.d("CSD","END CLICKED");
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
                try {
                    Date sd=ft.parse("2020-01-01");
                    invoiceDatePickerFragment.setStartDate(sd);
                    invoiceDatePickerFragment.setEndDate(new Date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                invoiceDatePickerFragment.show(getActivity().getSupportFragmentManager(), "invoiceDatePicker");
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rg1 = binding.radioGroupPeriod;
        rg2 = binding.radioGroupPeriod1;
        //  rg1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
        // rg2.clearCheck();
        rg1.setOnCheckedChangeListener(listener1);
        rg2.setOnCheckedChangeListener(listener2);

        setStatusLoading();

        viewmodel = InvoiceActivity.obtainViewModel(getActivity());

        binding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DailyIndentActivity) getActivity()).getReports();
            }
        });
        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                binding.merchantName.setText(merchant.getName());
                viewmodel.merchantName = merchant.getName();
                viewmodel.merchantAddress = merchant.getAddressFieldsConfig();
                viewmodel.getInvoiceItems();
            }
        });


        viewmodel.startDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                binding.radioBtnCustom.setChecked(true);
                if(dateChanged){
                    if(viewmodel.startDateCalender != null){
                        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.startDateCalender.getTime());
                        binding.startDatePicker.setText(date);
                        if (viewmodel.endDateCalender != null) {
                            Log.d("CSD","START- START DATE CAL"+viewmodel.startDateCalender.getTime().toString());
                            Log.d("CSD","START- END DATE CAL"+viewmodel.endDateCalender.getTime().toString());
                            viewmodel.startDate=viewmodel.startDateCalender.getTimeInMillis();
                            viewmodel.endDate=viewmodel.endDateCalender.getTimeInMillis();
                            viewmodel.selectedPeriod="Custom";
                            setData();
                        }
                    }
                }
            }
        });

        viewmodel.endDateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                binding.radioBtnCustom.setChecked(true);
                if(dateChanged){
                    if(viewmodel.endDateCalender != null){
                        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY_WITH_SEPARATOR, viewmodel.endDateCalender.getTime());
                        binding.endDatePicker.setText(date);

                        if (viewmodel.startDateCalender != null) {
                            Log.d("CSD", "END - START DATE CAL" + viewmodel.startDateCalender.getTime().toString());
                            Log.d("CSD", "END - END DATE CAL" + viewmodel.endDateCalender.getTime().toString());
                            viewmodel.startDate=viewmodel.startDateCalender.getTimeInMillis();
                            viewmodel.endDate=viewmodel.endDateCalender.getTimeInMillis();
                            viewmodel.selectedPeriod="Custom";
                           setData();
                        }
                    }
                }
            }
        });

        viewmodel.customer.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(@Nullable Customer customer) {


                viewmodel.customerName = getName(customer);
                viewmodel.customerAddress = getAddress(customer);

                binding.name.setText(viewmodel.customerName);
                binding.address.setText(viewmodel.customerAddress);
            }
        });

        viewmodel.invoices.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(@Nullable List<Invoice> invoices) {
                if (viewmodel.isActionFromInvoiceHistory) {
                    viewmodel.latestInvoice = InvoiceUtil.getSelectedInvoice(invoices, viewmodel.invoiceId);

                } else {
                    viewmodel.latestInvoice = InvoiceUtil.getLatestInvoice(invoices);
                }
                binding.totalDueAtTop.setText(FormatUtil.getRupeePrefixedAmount(
                        InvoiceFragment.this.getActivity(),
                        viewmodel.latestInvoice.getInvoiceAmount(),
                        FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS
                ));

                binding.invoiceNumber.setText(viewmodel.latestInvoice.getInvoiceNumber());

                binding.invoiceDate.setText(FormatUtil.DATE_FORMAT_DMY.format(viewmodel.latestInvoice.getInvoiceDate()));

                binding.billingPeriod.setText(viewmodel.latestInvoice.getInvoicePeriod());

                if (viewmodel.latestInvoice.getDueDate() != null) {
                    binding.dueDate.setText(FormatUtil.DATE_FORMAT_DMY.format(viewmodel.latestInvoice.getDueDate()));
                    binding.dueDateLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.dueDateLayout.setVisibility(View.GONE);
                }

                if (viewmodel.latestInvoice.getInvoiceItems() != null) {
                    ArrayList<InvoiceItem> sortedInvoiceItems = InvoiceUtil.getSortedInvoiceItems(viewmodel.latestInvoice);

                    addInvoiceItemViews(sortedInvoiceItems);
                    binding.invoiceItemsLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.invoiceItemsLayout.setVisibility(View.GONE);
                }

                binding.totalDueAtBottom.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(viewmodel.latestInvoice.getInvoiceAmount()));


            }

        });

        if (BuildConfig.PRODUCT_FLAVOUR_MERCHANT == false)
            binding.updateInvoiceItemButton.setVisibility(View.VISIBLE);

        binding.updateInvoiceItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UpdateInvoiceActivity.class);
                intent.putExtra(Extras.CUSTOMER_ID, viewmodel.customerId);
                getContext().startActivity(intent);
            }
        });


        viewmodel.invoiceItemApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                }
            }
        });

        viewmodel.invoiceItemApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    binding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.invoiceItemApiCallError.setValue(false);
                }
            }
        });

        viewmodel.invoiceItemApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    setData();
                }
            }
        });

        viewmodel.networkError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    binding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
                    setStatusNetworkError();
                    viewmodel.invoiceItemApiCallSuccess.setValue(false);
                }
            }
        });
    }


    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rg2.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                rg2.clearCheck(); // clear the second RadioGroup!
                rg2.setOnCheckedChangeListener(listener2); //reset the listener

                //int selectedId = osrFragBinding.radioGroupPeriod.getCheckedRadioButtonId();
                radioButton =  binding.radioGroupPeriod.findViewById(checkedId);

                binding.startDatePicker.setText("Start date");
                binding.endDatePicker.setText("End date");

                viewmodel.startDateCalender = null;
                viewmodel.endDateCalender = null;
                viewmodel.selectedPeriod = radioButton.getText().toString();


                setData();
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                rg1.setOnCheckedChangeListener(null);
                rg1.clearCheck();
                rg1.setOnCheckedChangeListener(listener1);


                Log.e("CSD", "do the work");
            }
        }
    };

    private void setData() {
        viewmodel.filteredList = new ArrayList<>();
        viewmodel.filteredList = filterBills(viewmodel.dailyIndentInvoices.getDatewiseBills());
        if (viewmodel.filteredList.size()>0) {
            setHeaderProd();
            setRecyclerView();
            adapter.setItems(viewmodel.filteredList);
            setTotal(viewmodel.filteredList);
            setStatusReady();
        }else {
            setStatusNoData();
        }

    }
    private void setRecyclerView(){
        adapter = new DateWiseInvoiceItemAdapter(getActivity());
        adapter.setViewModel(DailyIndentActivity.obtainViewModel(this.getActivity()));

        RecyclerView.ItemDecoration dividerDecoration2 = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerDecoration2);
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setAdapter(adapter);
    }
    private List<DateWiseBills> filterBills(List<DateWiseBills> orgList) {
        List<DateWiseBills> list = new ArrayList<>();

            switch (viewmodel.selectedPeriod)
            {
                case "This Week":
                    viewmodel.previousBalanceUnpaid = 0.0;
                    Calendar cSun = Calendar.getInstance();
                  /*  if (cSun.get(Calendar.WEEK_OF_MONTH)==1)
                        cSun.add( Calendar.DAY_OF_WEEK,cSun.getFirstDayOfWeek() );
                    else*/
                        cSun.add( Calendar.DAY_OF_WEEK, -(cSun.get(Calendar.DAY_OF_WEEK)-1));
                    cSun.add( Calendar.DAY_OF_WEEK, -(cSun.get(Calendar.DAY_OF_WEEK)-1));
                    cSun.set(Calendar.HOUR_OF_DAY, 0);
                    cSun.set(Calendar.MINUTE, 0);
                    cSun.set(Calendar.SECOND, 0);
                    cSun.set(Calendar.MILLISECOND, 0);

                    System.out.println("Date " + cSun.getTime());
                    viewmodel.startDate=cSun.getTimeInMillis();

                    Calendar cSat = Calendar.getInstance();
                    cSat.add( Calendar.DAY_OF_WEEK, -(cSat.get(Calendar.DAY_OF_WEEK)-1)+6);
                    cSat.set(Calendar.HOUR_OF_DAY, 0);
                    cSat.set(Calendar.MINUTE, 0);
                    cSat.set(Calendar.SECOND, 0);
                    cSat.set(Calendar.MILLISECOND, 0);
                    viewmodel.endDate =cSat.getTimeInMillis();
                    for (DateWiseBills dwb : orgList){
                        Date dwbDate = new Date(dwb.getIndentDate());
                        Date startDate = new Date(viewmodel.startDate);
                        Date endDate = new Date(viewmodel.endDate);
                        if (dwbDate.before(startDate)) {
                            viewmodel.previousBalanceUnpaid += dwb.getDailyTotal();
                            viewmodel.previousBalanceUnpaid -= dwb.getAdvancePaid();
                        }

                        if (dwbDate.after(startDate) && dwbDate.before(endDate))
                            list.add(dwb);
                        else if (dwbDate.equals(startDate) || dwbDate.equals(endDate))
                            list.add(dwb);
                    }
                    return list;
                case "This Month":
                    viewmodel.previousBalanceUnpaid = 0.0;
                   return orgList;

                case "Custom":
                    viewmodel.previousBalanceUnpaid = 0.0;
                    for (DateWiseBills dwb : orgList){
                        Date dwbDate = new Date(dwb.getIndentDate());
                        Date startDate = new Date(viewmodel.startDate);
                        Date endDate = new Date(viewmodel.endDate);
                        if (dwbDate.before(startDate)) {
                            viewmodel.previousBalanceUnpaid += dwb.getDailyTotal();
                            viewmodel.previousBalanceUnpaid -= dwb.getAdvancePaid();
                        }
                        if (dwbDate.after(startDate) && dwbDate.before(endDate))
                            list.add(dwb);
                        else if (dwbDate.equals(startDate) || dwbDate.equals(endDate))
                            list.add(dwb);
                    }

                    return list;
            }

        return orgList;

    }

    private void setTotal(List<DateWiseBills> list) {
        binding.linearHeaderContentTotal.removeAllViews();
        LinearLayout parent1 = new LinearLayout(getContext());

        parent1.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent1.setOrientation(LinearLayout.HORIZONTAL);
        TextView name = new TextView(getContext());
        name.setText("TOTAL");
        name.setTextAppearance(getContext(), R.style.FontMedium);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(getResources().getColor(R.color.text_primary));

        parent1.addView(name);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent1.getLayoutParams();
        params.setMargins(14, 0, 14, 0);
        parent1.setLayoutParams(params);
        binding.linearHeaderContentTotal.addView(parent1);

        Double totalAmount = 0.0;
        Double totalAdvanceAmount = 0.0;
        for (DateWiseBills dwb : list) {
            totalAmount = dwb.getDailyTotal() + totalAmount;
            totalAdvanceAmount = dwb.getAdvancePaid() + totalAdvanceAmount;
        }
        LinearLayout parentAmount = new LinearLayout(getContext());

        parentAmount.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parentAmount.setOrientation(LinearLayout.HORIZONTAL);
        TextView amount = new TextView(getContext());
        amount.setText(FormatUtil.getRupeePrefixedAmount(
                getContext(),
                totalAmount,
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
        amount.setTextAppearance(getContext(), R.style.FontMedium);
        amount.setTypeface(null, Typeface.BOLD);
        amount.setTextColor(getResources().getColor(R.color.text_primary));

        parentAmount.addView(amount);
        LinearLayout.LayoutParams paramsAmount = (LinearLayout.LayoutParams) parentAmount.getLayoutParams();
        paramsAmount.setMargins(14, 0, 14, 0);
        parentAmount.setLayoutParams(paramsAmount);
        binding.linearHeaderContentTotal.addView(parentAmount);

        LinearLayout parentAdvancePaid = new LinearLayout(getContext());

        parentAdvancePaid.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parentAdvancePaid.setOrientation(LinearLayout.HORIZONTAL);
        TextView advancePaid = new TextView(getContext());
        advancePaid.setText(FormatUtil.getRupeePrefixedAmount(
                getContext(),
                totalAdvanceAmount,
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
        advancePaid.setTextAppearance(getContext(), R.style.FontMedium);
        advancePaid.setTypeface(null, Typeface.BOLD);
        advancePaid.setTextColor(getResources().getColor(R.color.text_primary));

        parentAdvancePaid.addView(advancePaid);
        LinearLayout.LayoutParams paramsAdvancePaid = (LinearLayout.LayoutParams) parentAdvancePaid.getLayoutParams();
        paramsAdvancePaid.setMargins(14, 0, 14, 0);
        parentAdvancePaid.setLayoutParams(paramsAdvancePaid);
        binding.linearHeaderContentTotal.addView(parentAdvancePaid);

        binding.linearPreviousMonth.setVisibility(View.VISIBLE);

        double pmBalance = viewmodel.latestInvoice.getInvoiceAmount() -(viewmodel.wholeMonthTotalInvAmt-totalAdvanceAmount);
        pmBalance = Math.abs(pmBalance);
        binding.previousMonthBalance.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(pmBalance));

        System.out.println("LA getInvoiceAmount"+viewmodel.latestInvoice.getInvoiceAmount());

        System.out.println("LA getInvoiceAmount"+viewmodel.wholeMonthTotalInvAmt);
        System.out.println("LA totalAdvanceAmount"+totalAdvanceAmount);
        System.out.println("LA pmBalance"+ pmBalance);

        //viewmodel.previousBalanceUnpaid += viewmodel.latestInvoice.getInvoiceAmount()-viewmodel.wholeMonthTotalInvAmt; 147955

        Double totalDue =  pmBalance +totalAmount+viewmodel.previousBalanceUnpaid-totalAdvanceAmount;

        System.out.println("LA totalAmount"+totalAmount);
        System.out.println("LA previousBalanceUnpaid-"+viewmodel.previousBalanceUnpaid);

       // Double totalDue = totalAmount +pmBalance - totalAdvanceAmount +viewmodel.previousBalanceUnpaid;
        if (viewmodel.previousBalanceUnpaid>0){
            binding.linearFilteredAdvanceOrPrevious.setVisibility(View.VISIBLE);
            binding.filteredAdvanceOrPrevious.setText(getResources().getString(R.string.previous_period_unpaid));
            binding.previousBalanceAtBottom.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(viewmodel.previousBalanceUnpaid));
        }else {
            binding.linearFilteredAdvanceOrPrevious.setVisibility(View.VISIBLE);
            binding.filteredAdvanceOrPrevious.setText(getResources().getString(R.string.advance_paid_label));
            binding.previousBalanceAtBottom.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(viewmodel.previousBalanceUnpaid));
        }
        if (viewmodel.previousBalanceUnpaid==0)
            binding.linearFilteredAdvanceOrPrevious.setVisibility(View.GONE);

        binding.totalDueAtBottom.setText(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(totalDue));

    }

    private void setHeaderProd() {
        binding.linearHeaderContent.removeAllViews();
        LinearLayout parent1 = new LinearLayout(getContext());

        parent1.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent1.setOrientation(LinearLayout.HORIZONTAL);
        TextView name = new TextView(getContext());
        name.setText("DATE");
        name.setTextAppearance(getContext(), R.style.FontMedium);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(getResources().getColor(R.color.text_primary));

        parent1.addView(name);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent1.getLayoutParams();
        params.setMargins(14, 0, 14, 0);
        parent1.setLayoutParams(params);
        binding.linearHeaderContent.addView(parent1);

        LinearLayout parentAmount = new LinearLayout(getContext());

        parentAmount.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parentAmount.setOrientation(LinearLayout.HORIZONTAL);
        TextView amount = new TextView(getContext());
        amount.setText("AMOUNT");
        amount.setTextAppearance(getContext(), R.style.FontMedium);
        amount.setTypeface(null, Typeface.BOLD);
        amount.setTextColor(getResources().getColor(R.color.text_primary));

        parentAmount.addView(amount);
        LinearLayout.LayoutParams paramsAmount = (LinearLayout.LayoutParams) parentAmount.getLayoutParams();
        paramsAmount.setMargins(14, 0, 14, 0);
        parentAmount.setLayoutParams(paramsAmount);
        binding.linearHeaderContent.addView(parentAmount);

        LinearLayout parentAdvancePaid = new LinearLayout(getContext());

        parentAdvancePaid.setLayoutParams(new LinearLayout.LayoutParams(240, ViewGroup.LayoutParams.WRAP_CONTENT));
        parentAdvancePaid.setOrientation(LinearLayout.HORIZONTAL);
        TextView advancePaid = new TextView(getContext());
        advancePaid.setText("ADVANCE PAID");
        advancePaid.setTextAppearance(getContext(), R.style.FontMedium);
        advancePaid.setTypeface(null, Typeface.BOLD);
        advancePaid.setTextColor(getResources().getColor(R.color.text_primary));

        parentAdvancePaid.addView(advancePaid);
        LinearLayout.LayoutParams paramsAdvancePaid = (LinearLayout.LayoutParams) parentAdvancePaid.getLayoutParams();
        paramsAdvancePaid.setMargins(14, 0, 14, 0);
        parentAdvancePaid.setLayoutParams(paramsAdvancePaid);
        binding.linearHeaderContent.addView(parentAdvancePaid);
        for (int i = 0; i < viewmodel.offeredProductList.size(); i++) {
            LinearLayout parent2 = new LinearLayout(getContext());
            parent2.setLayoutParams(new LinearLayout.LayoutParams(90, ViewGroup.LayoutParams.WRAP_CONTENT));
            parent2.setOrientation(LinearLayout.HORIZONTAL);
            TextView shortCode = new TextView(getContext());
            shortCode.setText(viewmodel.offeredProductList.get(i).getProductShortCode());
            shortCode.setTextAppearance(getContext(), R.style.FontMedium);
            shortCode.setTypeface(null, Typeface.BOLD);
            shortCode.setTextColor(getResources().getColor(R.color.text_primary));
            parent2.addView(shortCode);

            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) parent2.getLayoutParams();
            params1.setMargins(14, 0, 14, 0);
            parent2.setLayoutParams(params1);
            binding.linearHeaderContent.addView(parent2);
        }

    }


    private void addInvoiceItemViews(final ArrayList<InvoiceItem> sortedInvoiceItems) {

        for (final InvoiceItem invoiceItem : sortedInvoiceItems) {
            viewmodel.previousMonthUnpaid = 0.0;
            if (invoiceItem.getItem().equalsIgnoreCase("Previous Balance")) {
                viewmodel.previousMonthUnpaid = invoiceItem.getAmount();

                break;
            }
            //   binding.invoiceItemsContainer.addView(invoiceItemView);
        }
    }

    private String getName(Customer customer) {
        StringBuilder stringBuilder = new StringBuilder();
        if (customer.getFirstName() != null && customer.getFirstName().trim().length() > 0) {
            stringBuilder.append(customer.getFirstName().trim());
        }
        if (customer.getLastName() != null && customer.getLastName().trim().length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(customer.getLastName().trim());
        }
        return stringBuilder.toString();
    }

    private String getAddress(Customer customer) {
        StringBuilder stringBuilder = new StringBuilder();
        if (customer.getDoorNumber() != null && customer.getDoorNumber().trim().length() > 0) {
            stringBuilder.append(customer.getDoorNumber().trim());
        }
        if (customer.getAddressLine1() != null && customer.getAddressLine1().trim().length() > 0) {
            String addressLine1 = Utils.getAddressLine1(getContext(), customer.getAddressLine1()).trim();
            if (!addressLine1.isEmpty()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(addressLine1);
            }
        }
        if (customer.getAddressLine2() != null && customer.getAddressLine2().trim().length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(customer.getAddressLine2().trim());
        }
        return stringBuilder.toString();
    }

    private ImageLoader imageLoader;

    private void openDialog(String billImgUrl) {


        imageLoader = VolleyManager.getInstance(getActivity()).getImageLoader();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.bill_image_preview_frag, null);

        final NetworkImageView billImg = view.findViewById(R.id.ivBillCopy);


        billImg.setImageUrl(billImgUrl, imageLoader);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();
        view.findViewById(R.id.cancelPreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }


    private void setStatusNetworkError() {
        binding.contentView.setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
        binding.noDataView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        binding.contentView.setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
        binding.noDataView.getRoot().setVisibility(View.GONE);
    }


    private void setStatusReady() {
        binding.contentView.setVisibility(View.VISIBLE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
        binding.noDataView.getRoot().setVisibility(View.GONE);
    }
    private void setStatusNoData() {
        binding.contentView.setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
        binding.noDataView.getRoot().setVisibility(View.VISIBLE);
    }
}
