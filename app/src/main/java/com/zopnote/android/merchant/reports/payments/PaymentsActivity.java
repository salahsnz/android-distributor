package com.zopnote.android.merchant.reports.payments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.zopnote.android.merchant.databinding.PaymentsActBinding;
import com.zopnote.android.merchant.util.ActivityUtils;

public class PaymentsActivity extends AppCompatActivity {
    private PaymentsActBinding binding;
    private PaymentsViewModel viewmodel;
    private int selectedMonth;
    private String selectedMonthString;
    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.payments_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        initVariables();

        setupDatePickerSpinner();

        setupViewFragment();
    }

    private void initVariables() {
        selectedMonth = viewmodel.month;
        selectedMonthString = viewmodel.monthString;
        selectedYear = viewmodel.year;
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
        final View view = inflater.inflate(R.layout.month_year_layout, null);
        builder.setView(view);

        builder.setTitle(R.string.collection_report_select_month_year_dialog_message);

        setupSpinners(view);

        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewmodel.month = selectedMonth;
                viewmodel.monthString = selectedMonthString;
                viewmodel.year = selectedYear;
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
        Spinner monthsSpinner = view.findViewById(R.id.monthSpinner);
        ArrayAdapter<CharSequence> monthsArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
        monthsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsSpinner.setAdapter(monthsArrayAdapter);

        //set current month
        monthsSpinner.setSelection(viewmodel.month, false);
        monthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonthString = (String) parent.getItemAtPosition(position);
                selectedMonth = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner yearSpinner = view.findViewById(R.id.yearSpinner);
        ArrayAdapter<CharSequence> yearsArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);
        yearsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        yearSpinner.setAdapter(yearsArrayAdapter);

        //set current year
        int yearPos = yearsArrayAdapter.getPosition(String.valueOf(viewmodel.year));
        if(yearPos != -1){
            yearSpinner.setSelection(yearPos, false);
        }

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setToolbarDate() {
        String displayMonthYear = String.format("%s %s", viewmodel.monthString, viewmodel.year);
        binding.monthYearText.setText(displayMonthYear);
    }

    private void setupViewFragment() {
        PaymentsFragment paymentsFragment = (PaymentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);
        if(paymentsFragment == null){
            paymentsFragment = PaymentsFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), paymentsFragment, R.id.contentView);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        binding.title.setText(this.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_PAYMENTS, "PaymentsActivity");
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

    public static PaymentsViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        PaymentsViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(PaymentsViewModel.class);

        return viewModel;
    }
}
