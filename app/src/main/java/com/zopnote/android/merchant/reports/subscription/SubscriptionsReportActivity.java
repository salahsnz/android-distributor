package com.zopnote.android.merchant.reports.subscription;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.SubscriptionsReportActBinding;
import com.zopnote.android.merchant.reports.collection.PaymentFilterOption;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SubscriptionsReportActivity extends AppCompatActivity {

    private SubscriptionsReportViewModel viewmodel;
    private SubscriptionsReportActBinding binding;
    private String agencyName = "";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.subscriptions_report_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();
        setupArgs();

        viewmodel.canSavePdf.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean canDownload) {
                if (canDownload) {
                    binding.saveAsPdfSubscription.setVisibility(View.VISIBLE);
                }
            }
        });
        viewmodel.reportPdfUri.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String uri) {
                binding.progressBar.setVisibility(View.GONE);
                binding.saveAsPdfSubscription.setVisibility(View.VISIBLE);
                Utils.showSuccessToast(SubscriptionsReportActivity.this,getString(R.string.save_as_pdf_success_subscription) , Toast.LENGTH_LONG);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.distributor.myfileprovider", new File(uri));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_subscription_report)));
            }
        });
        binding.saveAsPdfSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                openRouteDialog();
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                agencyName = merchant.getName();
            }
        });

        setupViewFragment();
    }

    private void setupArgs() {

        if (getIntent().hasExtra(Extras.PRODUCT)) {
            viewmodel.filterProduct = getIntent().getStringExtra(Extras.PRODUCT);
            viewmodel.isFilter = true;
        } else {
            viewmodel.isFilter = false;
        }


    }



    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static SubscriptionsReportViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        SubscriptionsReportViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(SubscriptionsReportViewModel.class);

        return viewModel;
    }

    private void setupViewFragment() {
        SubscriptionsReportFragment paymentsFragment = (SubscriptionsReportFragment) getSupportFragmentManager().findFragmentById(R.id.contentView);
        if(paymentsFragment == null){
            paymentsFragment = SubscriptionsReportFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), paymentsFragment, R.id.contentView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.VIEW_SUBSCRIPTIONS_REPORT, "SubscriptionsReport");
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


    private void openRouteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String buttonText =this.getResources().getString(R.string.save_as_pdf);
        String selectRoute =this.getResources().getString(R.string.select_route);
        builder.setTitle(selectRoute);
        final List<String> choices = new ArrayList<>();
        choices.add("All");
        for (int i = 0; i<viewmodel.reportItems.size(); i++) {

            if (viewmodel.reportItems.get(i) instanceof RouteHeader) {
                choices.add(((RouteHeader) viewmodel.reportItems.get(i)).getName());
            }
        }
        final boolean[] checkedItems = new boolean[choices.size()];
        builder.setMultiChoiceItems(choices.toArray(new String[]{}), checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which,
                                        final boolean isChecked) {
                        AlertDialog Aldialog = (AlertDialog) dialog;
                        ListView v = Aldialog.getListView();
                        if (which == 0 ){
                            int i = 0;
                            while (i < choices.size()) {
                                v.setItemChecked(i, isChecked);
                                checkedItems[i] = isChecked;
                                i++;
                            }
                        }else {
                            //uncheck all
                            v.setItemChecked(0, false);
                            checkedItems[0] = false;

                            checkedItems[which] = isChecked;
                        }

                    }
                });

        builder.setPositiveButton(buttonText,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNotSelected(checkedItems)){
                    Toast.makeText(SubscriptionsReportActivity.this,SubscriptionsReportActivity.this.getResources().getString(R.string.route_error_message),Toast.LENGTH_SHORT).show();
                }else {
                    if (isPermissionEnabledStorage()) {


                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.saveAsPdfSubscription.setVisibility(View.GONE);
                    List<String> selectedItems = new ArrayList<>();
                    for (int i = 1; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            selectedItems.add(choices.get(i));
                        }
                    }
                    SubscriptionReportPDF.build(SubscriptionsReportActivity.this, agencyName, viewmodel, selectedItems);
                    dialog.dismiss();
                    }
                }
            }
        });






        // int currentChoice = viewmodel.filterType.ordinal(); //TODO: verify
        builder.create().show();
    }

    private boolean isNotSelected(boolean[] checkedItems){
        for (int i=0; i<checkedItems.length; i++){
            if (checkedItems[i]){
                return false;
            }
        }
        return true;
    }

    private boolean isPermissionEnabledStorage() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   //do nothing
                } else {
                    Toast.makeText(this,R.string.permissions_write_external_Storage_permission_settings_instruction, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
