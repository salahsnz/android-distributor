package com.zopnote.android.merchant.addsubscription;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.databinding.AddSubscriptionActBinding;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.Validatable;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerFragment;


public class AddSubscriptionActivity extends AppCompatActivity {

    private final static String FRAGMENT_TAG_SUBSCRIPTION = "subscription_fragment";
    private final static String FRAGMENT_TAG_DATE_PICKER = "date_picker_fragment";
    private final static String FRAGMENT_TAG_REVIEW = "review_fragment";
    private ProgressDialog progressDialog;
    private AddSubscriptionActBinding binding;
    private AddSubscriptionViewModel viewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.add_subscription_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        setupArgs();

        viewmodel.step.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer step) {
                String stepTitle;
                switch (step) {
                    case 1:
                        // not using title for add subscription step
                        stepTitle = null;
                        break;
                    case 2:
                        stepTitle = getResources().getString(R.string.add_subscription_step_start_date);
                        break;
                    case 3:
                        stepTitle = getResources().getString(R.string.add_customer_step_review);
                        break;
                    default:
                        stepTitle = null;
                        break;
                }
                if (stepTitle != null) {
                    binding.stepTitle.setText(stepTitle);
                    binding.stepTitle.setVisibility(View.VISIBLE);
                } else {
                    binding.stepTitle.setVisibility(View.GONE);
                }
            }
        });

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(AddSubscriptionActivity.this);
                    progressDialog.setMessage(getResources().getString(R.string.add_customer_running_message));
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
                    Utils.showFailureToast(AddSubscriptionActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(AddSubscriptionActivity.this,
                            getResources().getString(R.string.add_subscription_success_message),
                            Toast.LENGTH_LONG);
                    finish();

                }
            }
        });

      /*  viewmodel.generateInvoiceApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(AddSubscriptionActivity.this);
                    progressDialog.setMessage(AddSubscriptionActivity.this.getResources().getString(R.string.generate_invoice_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.generateInvoiceApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.generateInvoiceApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(AddSubscriptionActivity.this,
                            AddSubscriptionActivity.this.getResources().getString(R.string.generate_invoice_success_message),
                            Toast.LENGTH_LONG);
                    viewmodel.generateInvoiceApiCallSuccess.setValue(false);
                    finish();
                }
            }
        });

        viewmodel.generateInvoiceApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(AddSubscriptionActivity.this,
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.generateInvoiceApiCallError.setValue(false);
                }
            }
        });*/

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextFragment();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevFragment();
            }
        });

        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(AddSubscriptionActivity.this)) {
                    viewmodel.addSubscription();
                }
            }
        });

        setupFragment(viewmodel.step.getValue());
    }

    private void setupArgs() {
        viewmodel.customerId = getIntent().getStringExtra(Extras.CUSTOMER_ID);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void nextFragment() {
        Validatable currentFragment = (Validatable) getOrCreateFragment(viewmodel.step.getValue());
        if (currentFragment.validate()) {
            setupFragment(viewmodel.step.getValue() + 1);
        }
    }

    private void prevFragment() {
        setupFragment(viewmodel.step.getValue() - 1);
    }

    private void setupFragment(int step) {
        Fragment fragment = getOrCreateFragment(step);

        ActivityUtils.replaceFragmentInActivity(
                getSupportFragmentManager(),
                fragment,
                R.id.contentFrame,
                getFragmentTag(step));

        viewmodel.step.setValue(step);
        updatePrevNextButtonVisibility(step);
    }

    private Fragment getOrCreateFragment(int step) {
        Fragment fragment;
        switch (step) {
            case 1:
                fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTag(step));
                if (fragment == null) {
                    fragment = SubscriptionsFragment.newInstance();
                }
                break;
            case 2:
                fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTag(step));
                if (fragment == null) {
                    fragment = DateFragment.newInstance();
                }
                break;
            case 3:
                fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTag(step));
                if (fragment == null) {
                    fragment = ReviewSubscriptionFragment.newInstance();
                }
                break;
            default:
                fragment = null;
                break;
        }

        return fragment;
    }

    private String getFragmentTag(int step) {
        switch (step) {
            case 1:
                return FRAGMENT_TAG_SUBSCRIPTION;
            case 2:
                return FRAGMENT_TAG_DATE_PICKER;
            case 3:
                return FRAGMENT_TAG_REVIEW;
            default:
                return null;
        }
    }

    private void updatePrevNextButtonVisibility(int step) {
        if (step > 1) {
            binding.backButton.setVisibility(View.VISIBLE);
        } else {
            binding.backButton.setVisibility(View.GONE);
        }

        if (step < 3) {
            binding.nextButton.setVisibility(View.VISIBLE);
        } else {
            binding.nextButton.setVisibility(View.GONE);
        }

        if (step == 3) {
            binding.submitButton.setVisibility(View.VISIBLE);
        } else {
            binding.submitButton.setVisibility(View.GONE);
        }
    }

    public static AddSubscriptionViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        AddSubscriptionViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(AddSubscriptionViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.ADD_CUSTOMER, "AddSubscriptionActivity");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // remember to call our onBackPressed
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (viewmodel.step.getValue() > 1) {
            prevFragment();
        } else if (viewmodel.isEdited()) {
            String message = getResources().getString(R.string.unsaved_add_subscription_changes_error_message);
            String positiveButtonText = getResources().getString(R.string.exit_label);
            String negativeButtonText = getResources().getString(R.string.cancel_label);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AddSubscriptionActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // no-op
                        }
                    })
                    .create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
    }
}
