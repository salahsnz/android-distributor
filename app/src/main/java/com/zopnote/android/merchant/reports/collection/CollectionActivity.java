package com.zopnote.android.merchant.reports.collection;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.CollectionActBinding;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CollectionActivity extends AppCompatActivity {
    private static final String LOG_TAG = "CollectionActivity";
    private CollectionActBinding binding;
    private CollectionViewModel viewmodel;
    private List<String> tabs;
    private ViewPager viewPager;
    private String selectedRoute;
    private PaymentFilterOption selectedFilterType;
    private int selectedMonth;
    private int selectedYear;
    private int dayFrom;
    private int dayTo;
    private boolean isRouteSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.collection_act);

        setupArgs();

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init(selectedMonth, selectedYear, selectedFilterType);

        dayFrom = viewmodel.dayFrom;
        dayTo = viewmodel.dayTo;
        setupDatePickerSpinner();

        setupTabs();

        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    getReports();
                }
            }
        });

        binding.filterTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });

        setFilterLabel();
    }

    private void setupArgs() {

        selectedMonth = getIntent().getIntExtra(Extras.MONTH, -1);
        selectedYear = getIntent().getIntExtra(Extras.YEAR, -1);


        if(selectedMonth == -1 || selectedYear == -1){
            initVariables();
        }

        String selectedFilter = getIntent().getStringExtra(Extras.FILTER_TYPE);
        if(selectedFilter != null){
            selectedFilterType = PaymentFilterOption.valueOf(selectedFilter);
        }else{
            selectedFilterType = PaymentFilterOption.BILLED;
        }

        selectedRoute = getIntent().getStringExtra(Extras.ROUTE);
    }

    private void initVariables() {
        Calendar calendar = Calendar.getInstance();
        selectedMonth = calendar.get(Calendar.MONTH); //Jan month is 0
        selectedYear = calendar.get(Calendar.YEAR);

    }

    private void setupDatePickerSpinner() {

        setToolbarDate();

        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    setToolbarDate();
                }
            }
        });

        binding.titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateSelectionDialog();
            }
        });
    }

    private void showDateSelectionDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.day_from_day_to, null);
        builder.setView(view);

        builder.setTitle(R.string.collection_report_select_day_from_to_dialog_message);
        setupSpinners(view);

        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewmodel.dayFrom = dayFrom;
                viewmodel.dayTo = dayTo;
                viewmodel.dateChanged.setValue(true);
            }
        });

        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(false);
        builder.show();
    }



    private void setupSpinners(View view) {
        Spinner dayFromSpinner = view.findViewById(R.id.dayFromSpinner);
        ArrayAdapter<CharSequence> dayFromArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.days, android.R.layout.simple_spinner_item);
        dayFromArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dayFromSpinner.setAdapter(dayFromArrayAdapter);

        int dayFromPos = dayFromArrayAdapter.getPosition(String.valueOf(viewmodel.dayFrom));
        if(dayFromPos != -1){
            dayFromSpinner.setSelection(dayFromPos, false);
        }

        dayFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dayFrom = Integer.parseInt((String) parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner dayToSpinner = view.findViewById(R.id.dayToSpinner);
        ArrayAdapter<CharSequence> dayToArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.days, android.R.layout.simple_spinner_item);
        dayToArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dayToSpinner.setAdapter(dayToArrayAdapter);

        int dayToPos = dayToArrayAdapter.getPosition(String.valueOf(viewmodel.dayTo));
        if(dayToPos != -1){
            dayToSpinner.setSelection(dayToPos, false);
        }
        dayToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dayTo = Integer.parseInt((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setupTabs() {

        tabs = new ArrayList<>(10);

        final CollectionRoutesCategoryPagerAdapter pagerAdapter =
                new CollectionRoutesCategoryPagerAdapter(getSupportFragmentManager(), tabs);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                setScreenName();
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                List<String> routes = merchant.getRoutes();

                for (int i= 0; i<routes.size(); i++){
                    String routeName = routes.get(i);
                    if( !tabs.contains(routeName)){
                        tabs.add(routeName);
                    }
                }
                pagerAdapter.notifyDataSetChanged();

                if(selectedRoute != null){
                    int selectedRouteIndex = tabs.indexOf(selectedRoute);
                    if( selectedRouteIndex != -1 && ! isRouteSet){
                        viewPager.setCurrentItem(selectedRouteIndex);
                        isRouteSet = true;
                    }
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();
                getReports();

                //to avoid calling api multiple times due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });
    }

    public void getReports() {

        if (NetworkUtil.isNetworkAvailable(this)) {
            viewmodel.getCollectionReport();
        }else{
            viewmodel.networkError.postValue(true);
        }
    }

    private void setToolbarDate() {
        String displayMonthYear = String.format("%s %s", viewmodel.monthString, viewmodel.year);
        binding.monthYearText.setText(displayMonthYear);

        String displayDayFromTo = String.format("%s To %s", viewmodel.dayFrom, viewmodel.dayTo);
        binding.dayFromTo.setText(displayDayFromTo);
    }


    @Override
    protected void onResume() {
        super.onResume();

        setScreenName();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static CollectionViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        CollectionViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(CollectionViewModel.class);

        return viewModel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter by");
        List<String> choices = new ArrayList<>();
        for (PaymentFilterOption paymentFilterOption: PaymentFilterOption.values()) {
            choices.add(paymentFilterOption.getDisplayName());
        }
        int currentChoice = viewmodel.filterType.ordinal(); //TODO: verify
        builder.setSingleChoiceItems(choices.toArray(new String[]{}),
                currentChoice,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PaymentFilterOption selectedChoice = PaymentFilterOption.values()[which];
                        viewmodel.filterType = selectedChoice;
                        dialog.dismiss();
                        setFilterLabel();

                        viewmodel.filterTypeChanged.setValue(true);
                    }
                });
        builder.create().show();
    }

    private void setFilterLabel() {
        binding.filterTypeText.setText(viewmodel.filterType.getDisplayName());
    }

    private void setScreenName() {

        if (tabs.size() == 0) {
            return;
        }

        int currentItem = viewPager.getCurrentItem();
        String currentTab = tabs.get(currentItem);
        String screenName = String.format(ScreenName.COLLECTION + " - %s", currentTab);

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, screenName, "CollectionActivity");
    }
}
