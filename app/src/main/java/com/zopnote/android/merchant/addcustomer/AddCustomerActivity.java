package com.zopnote.android.merchant.addcustomer;

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
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.AddCustomerActBinding;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.Validatable;

public class AddCustomerActivity extends AppCompatActivity {

    private static String LOG_TAG = AddCustomerActivity.class.getSimpleName();
    private static boolean DEBUG = false;

    private final static String FRAGMENT_TAG_MOBILE_NUMBER = "mobile_number_fragment";
    private final static String FRAGMENT_TAG_SUBSCRIPTION = "subscription_fragment";
    private final static String FRAGMENT_TAG_REVIEW = "review_fragment";

    private AddCustomerActBinding binding;
    private AddCustomerViewModel viewmodel;

    private ProgressDialog progressDialog;
    private boolean addingSubscriptionView =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.add_customer_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        viewmodel.step.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer step) {
                String stepTitle;
                switch (step) {
                    case 1:
                        stepTitle = getResources().getString(R.string.add_customer_step_profile);
                        binding.pasteRouteButton.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        // hide title for subscription selection
                        stepTitle = null;
                        binding.pasteRouteButton.setVisibility(View.GONE);
                        addingSubscriptionView = true;
                        break;
                    case 3:
                        stepTitle = getResources().getString(R.string.add_customer_step_review);
                        binding.pasteRouteButton.setVisibility(View.GONE);
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
                    progressDialog = new ProgressDialog(AddCustomerActivity.this);
                    progressDialog.setMessage(AddCustomerActivity.this.getResources().getString(R.string.add_customer_running_message));
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
                    Utils.showFailureToast(AddCustomerActivity.this, viewmodel.apiCallErrorMessage, Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(AddCustomerActivity.this, AddCustomerActivity.this.getResources().getString(R.string.add_customer_success_message), Toast.LENGTH_LONG);
                    AddCustomerActivity.this.finish();
                }
            }
        });

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                System.out.println("Merchant id: " + merchant.getId());
            }
        });
        binding.reviewAndSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validatable currentFragment = (Validatable) getOrCreateFragment(viewmodel.step.getValue());
                if (currentFragment.validate()) {
                    setupFragment(viewmodel.step.getValue() + 2);
                }
            }
        });

      /*  binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextFragment();
            }
        });*/

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevFragment();
            }
        });

        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(AddCustomerActivity.this)) {
                    saveRouteAndAddressLine2Info();
                    viewmodel.addCustomer();
                }
            }
        });

        setupFragment(viewmodel.step.getValue());
    }

    private void saveRouteAndAddressLine2Info() {
        Prefs.putString(AppConstants.PREFS_LAST_USED_ROUTE, viewmodel.route);
        Prefs.putString(AppConstants.PREFS_LAST_USED_ADDRESS_LINE2, viewmodel.addressLine2);
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
        if (viewmodel.step.getValue() == 3 && !addingSubscriptionView) {
            setupFragment(viewmodel.step.getValue() - 2);
            return;
        }
         setupFragment(viewmodel.step.getValue() - 1);
        addingSubscriptionView = false;
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
                    fragment = ProfileFragment.newInstance();
                }
                break;
            case 2:
                fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTag(step));
                if (fragment == null) {
                    fragment = SubscriptionsFragment.newInstance();
                }
                break;
            case 3:
                fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTag(step));
                if (fragment == null) {
                    fragment = ReviewFragment.newInstance();
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
                return FRAGMENT_TAG_MOBILE_NUMBER;
            case 2:
                return FRAGMENT_TAG_SUBSCRIPTION;
            case 3:
                return FRAGMENT_TAG_REVIEW;
            default:
                return null;
        }
    }

    private void updatePrevNextButtonVisibility(int step) {
        if (step > 1) {
            binding.backButton.setVisibility(View.VISIBLE);
           // binding.nextButton.setText(getString(R.string.review_and_submit_label));
            binding.reviewAndSubmit.setVisibility(View.GONE);
        } else {
          //  binding.nextButton.setText(getString(R.string.add_subscription_label));
            binding.reviewAndSubmit.setVisibility(View.VISIBLE);
            binding.backButton.setVisibility(View.GONE);
        }

        if (step < 3) {
       //     binding.nextButton.setVisibility(View.VISIBLE);
        } else {
       //     binding.nextButton.setVisibility(View.GONE);
        }

        if (step == 3) {
            binding.submitButton.setVisibility(View.VISIBLE);
            binding.reviewAndSubmit.setVisibility(View.GONE);
        } else {
            binding.submitButton.setVisibility(View.GONE);
        }
    }

    public static AddCustomerViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        AddCustomerViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(AddCustomerViewModel.class);

        return viewModel;
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.ADD_CUSTOMER, "AddCustomerActivity");

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
            String message =AddCustomerActivity.this.getResources().getString(R.string.unsaved_changes_error_message);
            String positiveButtonText = AddCustomerActivity.this.getResources().getString(R.string.exit_label);
            String negativeButtonText = AddCustomerActivity.this.getResources().getString(R.string.cancel_label);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AddCustomerActivity.super.onBackPressed();
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
