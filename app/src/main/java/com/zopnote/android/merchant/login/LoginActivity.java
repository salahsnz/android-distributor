package com.zopnote.android.merchant.login;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.MyApplication;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.databinding.LoginActBinding;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.registermerchant.RegisterMerchantActivity;
import com.zopnote.android.merchant.util.ActivityUtils;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Installation;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.Validatable;

/**
 * Created by nmohideen on 14/02/18.
 */

public class LoginActivity extends AppCompatActivity {

    private static final int ACTIVITY_ACCOUNT_KIT_REQUEST = 1;


    private final static String FRAGMENT_TAG_ENTER_MOBILE = "enter_mobile_fragment";
    private final static String FRAGMENT_TAG_OTP = "otp_fragment";

    private LoginActBinding binding;
    public static LoginViewModel viewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.login_act);

        viewmodel = obtainViewModel(this);

        setupClickListeners();

        setupModelObservers();

        if (Installation.DEBUG_MODE) {
            Prefs.putBoolean(AppConstants.PREFS_APP_INIT_COMPLETE, true);
            startNextActivity();
        }
    }

    private void setupClickListeners() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewmodel.isError.setValue(false);
                viewmodel.step.setValue(1);
                viewmodel.isOTPSend = false;
                binding.linearContent.setVisibility(View.VISIBLE);
                setupFragment(viewmodel.step.getValue());

            }
        });

        binding.mobileNumberSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewmodel.isError.setValue(false);

                Validatable currentFragment = (Validatable) getOrCreateFragment(viewmodel.step.getValue());
                if (currentFragment.validate()) {
                    if (viewmodel.isOTPSend) {
                        viewmodel.verifyOTP();
                    } else {
                        viewmodel.sendOTP();
                    }
                }

            }
        });
    }

    private void setupModelObservers() {

        viewmodel.isRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

        viewmodel.isError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isError) {
                if (isError) {
                    binding.errorMessage.setVisibility(View.VISIBLE);
                    binding.button.setVisibility(View.VISIBLE);

                    binding.linearContent.setVisibility(View.GONE);
                } else {
                    binding.errorMessage.setVisibility(View.GONE);
                    binding.button.setVisibility(View.GONE);

                    binding.linearContent.setVisibility(View.VISIBLE);

                }
            }
        });

        if (BuildConfig.PRODUCT_FLAVOUR_MERCHANT) {
            viewmodel.isAuthCallSuccess.observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean success) {
                    if (success) {
                        firebaseSignIn();
                    }
                }
            });
            viewmodel.isNewUser.observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isNewUser) {
                    if (isNewUser) {
                        Prefs.putString(AppConstants.PREFS_SIGNED_IN_MOBILE_NUMBER, viewmodel.mobileNumber);
                        startSignUpActivity();
                    }
                }
            });
        } else {
            viewmodel.isAuthCallSuccess.observe(this, new Observer<Boolean>() {

                @Override
                public void onChanged(@Nullable Boolean success) {
                    if (success) {
                        validate();
                    }
                }
            });
        }

        viewmodel.sendOTPCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

        viewmodel.sendOTPCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isError) {
                if (isError) {
                    Utils.showFailureToast(LoginActivity.this,
                            "OTP send failed",
                            Toast.LENGTH_LONG);

                }
            }
        });


        viewmodel.sendOTPCallSuccess.observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    viewmodel.isOTPSend = true;
                    nextFragment();
                }
            }
        });


        viewmodel.verifyOTPCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

        viewmodel.verifyOTPCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isError) {
                if (isError) {
                    Utils.showFailureToast(LoginActivity.this,
                            "OTP not verified",
                            Toast.LENGTH_LONG);
                }
            }
        });


        viewmodel.verifyOTPCallSuccess.observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    viewmodel.callAuthApi();
                }
            }
        });


        viewmodel.resendOTPCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });

        viewmodel.resendOTPCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isError) {
                if (isError) {
                    Utils.showFailureToast(LoginActivity.this,
                            "Resent OTP failed",
                            Toast.LENGTH_LONG);

                }
            }
        });


        viewmodel.resendOTPCallSuccess.observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    viewmodel.isOTPSend = true;
                    Utils.showSuccessToast( LoginActivity.this,
                            "OTP resent successfully",Toast.LENGTH_LONG);
                }
            }
        });

        viewmodel.step.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer step) {
                if (step ==3){ //int 3 is back code back
                    prevFragment();
                }


            }
        });
        setupFragment(viewmodel.step.getValue());
    }

    public void nextFragment() {
        Validatable currentFragment = (Validatable) getOrCreateFragment(viewmodel.step.getValue());
        if (currentFragment.validate()) {
            setupFragment(viewmodel.step.getValue() + 1);
        }
    }

    private void prevFragment() {
        setupFragment(viewmodel.step.getValue() - 2);

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
                    fragment = MobileNumberFragment.newInstance();
                }
                break;
            case 2:
                fragment = getSupportFragmentManager().findFragmentByTag(getFragmentTag(step));
                if (fragment == null) {
                    fragment = OTPFragment.newInstance();
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
                return FRAGMENT_TAG_ENTER_MOBILE;
            case 2:
                return FRAGMENT_TAG_OTP;
            default:
                return null;
        }
    }

    private void updatePrevNextButtonVisibility(int step) {

      /*  if (step < 1) {
            binding.mobileNumberSubmit.setVisibility(View.VISIBLE);
        } else {
            binding.mobileNumberSubmit.setVisibility(View.GONE);
        }*/


    }





  /*  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == ACTIVITY_ACCOUNT_KIT_REQUEST) {
            binding.title.setVisibility(View.VISIBLE);

            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.wasCancelled() || result.getError() != null) {
                viewmodel.isError.setValue(true);
                Analytics.logEvent(Event.ERROR_MOBILE_LOGIN);
            } else {
                viewmodel.isMobileVerificationSuccess = true;
                viewmodel.authorizationCode = result.getAuthorizationCode();
                viewmodel.callAuthApi();
            }
        }
    }*/

    private void firebaseSignIn() {
        viewmodel.isRunning.setValue(true);
        FirebaseAuth.getInstance().signInWithCustomToken(viewmodel.loginToken)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        viewmodel.isRunning.setValue(false);
                        // inform new id to interested parties
                        ((MyApplication) getApplication()).updateIds();

                        Prefs.putBoolean(AppConstants.PREFS_APP_INIT_COMPLETE, true);

                        Prefs.putString(AppConstants.PREFS_SIGNED_IN_MOBILE_NUMBER, viewmodel.mobileNumber);

                        sendSignupAnalyticsEvent();

                        startNextActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        viewmodel.isRunning.setValue(false);
                        viewmodel.isError.setValue(true);
                        Crashlytics.logException(e);
                    }
                });
    }

    private void sendSignupAnalyticsEvent() {
        new Analytics.Builder()
                .setEventName(Event.SIGN_UP)
                .addParam(Param.SIGN_UP_METHOD, "mobile-number")
                .logEvent();
    }

    private void startNextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        finish();
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(this, RegisterMerchantActivity.class);
        intent.putExtra(Extras.MERCHANT_STATUS, viewmodel.merchantStatus);
        startActivity(intent);
        finish();
    }

    public static LoginViewModel obtainViewModel(FragmentActivity activity) {
        // uid is not yet available; cannot use our ViewModelFactory since it depends on uid

        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(activity.getApplication());

        LoginViewModel viewModel = factory.create(LoginViewModel.class);

        return viewModel;
    }

    private void validate() {
        if (FirebaseRemoteConfig.getInstance().getString(AppConstants.REMOTE_CONFIG_A_GROUP).contains(viewmodel.mobileNumber)) {
            firebaseSignIn();
        } else {
            viewmodel.isError.setValue(true);
        }
    }
}