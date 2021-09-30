package com.zopnote.android.merchant.notifications.notificationpanel;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.NotificationDashboardActBinding;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.Utils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NotificationDashboardActivity extends AppCompatActivity {

    private ActionBar ab;
    private NotificationDashboardViewModel viewmodel;
    private ProgressDialog progressDialog;
    private NotificationDashboardActBinding binding;
    private RadioButton radioButton;
    private int index=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.notification_dashboard_act);

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(NotificationDashboardActivity.this);
                    progressDialog.setMessage(NotificationDashboardActivity.this.getResources().getString(R.string.sending_notification_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(NotificationDashboardActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(NotificationDashboardActivity.this,
                            NotificationDashboardActivity.this.getResources().getString(R.string.notification_sent_success_message),
                            Toast.LENGTH_LONG);
                    Prefs.putBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, true);
                    NotificationDashboardActivity.this.finish();
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                System.out.println("Merchant id: " + merchant.getId());
            }
        });

        binding.radioGroupMerchant.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = binding.radioGroupMerchant.getCheckedRadioButtonId();
                radioButton = (RadioButton) binding.radioGroupMerchant.findViewById(selectedId);
                index=binding.radioGroupMerchant.indexOfChild(radioButton);
                //Log.d("CSD","Text : "+radioButton.getText().toString());
            }
        });


        binding.sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewmodel.notiTitle = binding.notificationTitle.getText().toString().trim();
                viewmodel.notiMessage = binding.notificationBody.getText().toString().trim();
                viewmodel.actionUrl = binding.notificationActionUrl.getText().toString().trim();

                //Log.d("CSD","ID :"+index);
                switch (index){
                    case 0:
                        viewmodel.notiMode=AppConstants.NOTIFICATION_SINGLE;
                        break;
                    case 1:
                        viewmodel.notiMode=AppConstants.NOTIFICATION_EVERYONE;
                        break;
                }
                if (!(viewmodel.notiTitle.length()>0) &&!(viewmodel.notiMessage.length()>0)){
                    Toast.makeText(getApplicationContext(),"Title and message required",Toast.LENGTH_LONG).show();
                    return;
                }
                viewmodel.sendNotification();

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);



    }

    public static NotificationDashboardViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        NotificationDashboardViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(NotificationDashboardViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }




}
