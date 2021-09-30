package com.zopnote.android.merchant.indent;

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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.DailySubscription;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.IndentActBinding;
import com.zopnote.android.merchant.reports.subscription.RouteHeader;
import com.zopnote.android.merchant.reports.subscription.SubscriptionReportPDF;
import com.zopnote.android.merchant.reports.subscription.SubscriptionsReportActivity;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class IndentActivity extends AppCompatActivity {
    private static final String LOG_TAG = "IndentActivity";
    private IndentActBinding binding;
    private IndentViewModel viewmodel;
    private List<String> tabs;
    private ViewPager viewPager;
    public static final String ROUTE_ALL = "All";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.indent_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();


        setupDatePicker();

        boolean prefSwitchChecked = Prefs.getBoolean(AppConstants.PREFS_INDENT_SWITCH_IS_CHECKED, true);
        if(prefSwitchChecked){
            viewmodel.indentType = "changes";
        }else{
            viewmodel.indentType = "all";
        }

        setupTabs();

        binding.indentSwitch.setChecked(prefSwitchChecked);
        binding.indentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Prefs.putBoolean(AppConstants.PREFS_INDENT_SWITCH_IS_CHECKED, true);
                    viewmodel.indentType = "changes";
                    viewmodel.indentTypeChanged.setValue(true);
                }else{
                    Prefs.putBoolean(AppConstants.PREFS_INDENT_SWITCH_IS_CHECKED, false);
                    viewmodel.indentType = "all";
                    viewmodel.indentTypeChanged.setValue(true);
                }
            }
        });

        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    getReports();
                }
            }
        });


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
                Utils.showSuccessToast(IndentActivity.this,getString(R.string.save_as_pdf_success_indent) , Toast.LENGTH_LONG);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.myfileprovider", new File(uri));
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


    }

    private void setupTabs() {

        tabs = new ArrayList<>(10);

        final RoutesCategoryPagerAdapter pagerAdapter =
                new RoutesCategoryPagerAdapter(getSupportFragmentManager(), tabs);

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

                //add "All" tab
                if(routes.size() > 0){
                    if( !tabs.contains(ROUTE_ALL)){
                        tabs.add(ROUTE_ALL);
                    }
                }

                for (int i= 0; i<routes.size(); i++){
                    String routeName = routes.get(i);
                    if( !tabs.contains(routeName)){
                        tabs.add(routeName);
                    }
                }
                pagerAdapter.notifyDataSetChanged();
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
            viewmodel.getIndentReport();
        }else{
            viewmodel.networkError.postValue(true);
        }
    }

    private void setupDatePicker() {

        setDate();

        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged){
                    setDate();
                }
            }
        });

        binding.titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    private void setDate() {
        String date = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, viewmodel.purchaseCalender.getTime());
        binding.showDatePicker.setText(date);
    }

    private void openRouteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String buttonText =this.getResources().getString(R.string.save_as_pdf);
        String selectRoute =this.getResources().getString(R.string.select_route);
        builder.setTitle(selectRoute);
        final List<String> choices = new ArrayList<>();
        choices.add("All");
        for (DailySubscription dailySubscription: viewmodel.indentReport) {
            if (choices.contains(dailySubscription.getRoute()) || dailySubscription.getRoute().equalsIgnoreCase("All"))
                continue;
            choices.add(dailySubscription.getRoute());
        }
        final boolean[] checkedItems = new boolean[choices.size()];
        builder.setMultiChoiceItems(choices.toArray(new String[]{}), checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which,
                                        final boolean isChecked) {
                        AlertDialog Aldialog = (AlertDialog) dialog;
                        ListView v = Aldialog.getListView();
                        v.setItemChecked(which, isChecked);
                        checkedItems[which] = isChecked;
                       /* if (which == 0 ){
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
*/
                    }
                });

        builder.setPositiveButton(buttonText,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNotSelected(checkedItems)){
                    Toast.makeText(IndentActivity.this,IndentActivity.this.getResources().getString(R.string.route_error_message),Toast.LENGTH_SHORT).show();
                }else {
                    if (isPermissionEnabledStorage()) {


                        binding.progressBar.setVisibility(View.VISIBLE);
                        binding.saveAsPdfSubscription.setVisibility(View.GONE);
                        List<String> selectedItems = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                selectedItems.add(choices.get(i));
                            }
                        }
                        IndentPDF.build(IndentActivity.this,selectedItems,viewmodel);
                        //viewmodel.merchant.getValue().getName(), viewmodel.indentReport, selectedItems,viewmodel.purchaseCalender.getTime(),
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

    public static IndentViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        IndentViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(IndentViewModel.class);

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

    private void setScreenName() {

        if (tabs.size() == 0) {
            return;
        }

        int currentItem = viewPager.getCurrentItem();
        String currentTab = tabs.get(currentItem);
        String screenName = String.format(ScreenName.INDENT + " - %s", currentTab);

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, screenName, "IndentActivity");
    }
}
