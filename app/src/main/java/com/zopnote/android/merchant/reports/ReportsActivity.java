package com.zopnote.android.merchant.reports;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.databinding.ReportsActivityBinding;
import com.zopnote.android.merchant.reports.draftinvoice.DraftInvoiceReportActivity;
import com.zopnote.android.merchant.reports.onboarding.OnboardActivity;
import com.zopnote.android.merchant.reports.ordersummary.OrderSummaryReportActivity;
import com.zopnote.android.merchant.reports.payments.PaymentsActivity;
import com.zopnote.android.merchant.reports.settlement.SettlementReportActivity;
import com.zopnote.android.merchant.reports.subscription.SubscriptionsReportActivity;

public class ReportsActivity extends AppCompatActivity {

    private ReportsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.reports_activity);

        setupToolbar();

        ((TextView)binding.onboardingReport.findViewById(R.id.title)).setText(getResources().getString(R.string.onboarding_report_label));
        binding.onboardingReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logEvent(Event.NAV_ON_BOARD_REPORT);
                Intent intent = new Intent(ReportsActivity.this, OnboardActivity.class);
                startActivity(intent);
            }
        });

        ((TextView)binding.paymentsReport.findViewById(R.id.title)).setText(getResources().getString(R.string.payments_report_label));
        binding.paymentsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logEvent(Event.NAV_PAYMENT_REPORT);
                Intent intent = new Intent(ReportsActivity.this, PaymentsActivity.class);
                startActivity(intent);
            }
        });

        ((TextView)binding.subscriptionsReport.findViewById(R.id.title)).setText(getResources().getString(R.string.subscriptions_report_label));
        binding.subscriptionsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logEvent(Event.NAV_SUBSCRIPTION_REPORT);
                Intent intent = new Intent(ReportsActivity.this, SubscriptionsReportActivity.class);
                startActivity(intent);
            }
        });

        ((TextView)binding.draftInvoiceReport.findViewById(R.id.title)).setText(getResources().getString(R.string.draft_invoice_report_label));
        binding.draftInvoiceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logEvent(Event.NAV_DRAFT_INVOICE_REPORT);
                Intent intent = new Intent(ReportsActivity.this, DraftInvoiceReportActivity.class);
                startActivity(intent);
            }
        });

        ((TextView)binding.settlementReport.findViewById(R.id.title)).setText(getResources().getString(R.string.settlement_report_label));
        binding.settlementReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logEvent(Event.NAV_SETTLEMENT_REPORT);
                Intent intent = new Intent(ReportsActivity.this, SettlementReportActivity.class);
                startActivity(intent);
            }
        });

        ((TextView)binding.orderSummaryReport.findViewById(R.id.title)).setText(getResources().getString(R.string.orderSummary_report_label));
        binding.orderSummaryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logEvent(Event.NAV_ORDER_SUMMARY_REPORT);
                Intent intent = new Intent(ReportsActivity.this, OrderSummaryReportActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.REPORTS, "ReportsActivity");

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
}
