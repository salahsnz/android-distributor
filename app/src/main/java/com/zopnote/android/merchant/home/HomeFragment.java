package com.zopnote.android.merchant.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.activatemerchant.ActivateMerchantActivity;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.dailyindent.DailyIndentActivity;
import com.zopnote.android.merchant.notifications.notificationpanel.NotificationDashboardActivity;
import com.zopnote.android.merchant.products.ProductsActivity;
import com.zopnote.android.merchant.customers.CustomersActivity;
import com.zopnote.android.merchant.databinding.HomeFragBinding;
import com.zopnote.android.merchant.reports.ReportsActivity;
import com.zopnote.android.merchant.search.SearchActivity;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

/**
 * Created by nmohideen on 26/12/17.
 */

public class HomeFragment extends Fragment {

    private HomeFragBinding binding;
    private HomeViewModel viewmodel;
    private ProgressDialog progressDialog;
    public HomeFragment() {
        // Requires empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = HomeFragBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = HomeActivity.obtainViewModel(getActivity());
        viewmodel.init();

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {

         boolean isOndemand = false;
        if (merchant.getProductList().contains("Ondemand"))
            isOndemand = true;
                binding.gridview.setAdapter(new HomeGridAdapter(getActivity(),isOndemand));

                final boolean finalIsOndemand = isOndemand;
                binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(BuildConfig.PRODUCT_FLAVOUR_MERCHANT == false) {
                            switch (i) {
                                case 0:
                                        getActivity().startActivity(new Intent(getActivity(), CustomersActivity.class));
                                    break;
                                case 1:
                                        getActivity().startActivity(new Intent(getActivity(), ProductsActivity.class));
                                    break;
                                case 2:
                                    getActivity().startActivity(new Intent(getActivity(), DailyIndentActivity.class));
                                    break;
                                case 3:
                                    getActivity().startActivity(new Intent(getActivity(), ReportsActivity.class));

                                    break;

                                case 4:

                                    openDialogSendRemainder();
                                    break;

                                case 5:
                                    openDialog();
                                    break;

                            }
                        }else{
                            switch (i) {
                                case 0:
                                    if (finalIsOndemand)
                                        getActivity().startActivity(new Intent(getActivity(), SearchActivity.class));
                                    else
                                        getActivity().startActivity(new Intent(getActivity(), CustomersActivity.class));
                                    break;
                                case 1:
                                    if (finalIsOndemand)
                                        getActivity().startActivity(new Intent(getActivity(), CustomersActivity.class));
                                    else
                                        getActivity().startActivity(new Intent(getActivity(), ProductsActivity.class));
                                    break;
                                case 2:
                                    if (finalIsOndemand)
                                        getActivity().startActivity(new Intent(getActivity(), ProductsActivity.class));
                                    else
                                        getActivity().startActivity(new Intent(getActivity(), DailyIndentActivity.class));
                                    break;
                                case 3:

                                    getActivity().startActivity(new Intent(getActivity(), ReportsActivity.class));

                                    break;

                                case 4:

                                    openDialogSendRemainder();
                                    break;

                                case 5:
                                    openDialog();
                                    break;

                            }
                        }

                    }
                });

            }
        });


        setupApiCallObservers();
    }
    private void openDialogSendRemainder(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select Route");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);

        for (String route : viewmodel.merchant.getValue().getRoutes()){
            arrayAdapter.add(route);
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                viewmodel.sendPaymentRemainderSMS(strName);
                dialog.dismiss();

            }
        });

        builderSingle.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();
    }

    private void openDialog() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Options");


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item);
        arrayAdapter.add("Notification Dashboard");
        arrayAdapter.add("Activate Mobile Number");
        arrayAdapter.add("Subscriber View");
        arrayAdapter.add("Process Settlement");
        arrayAdapter.add("Reset Merchant");
        arrayAdapter.add("Activate Merchant");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    getActivity().startActivity(new Intent(getActivity(), NotificationDashboardActivity.class));
                }else if (which == 1){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    final View view = inflater.inflate(R.layout.add_add_mobile_number_dialog, null);

                    final EditText itemMobileEditText = view.findViewById(R.id.customerMobile);
                    itemMobileEditText.setHint("Enter Mobile Number");

                    builder.setView(view);

                    builder.setMessage("Add Mobile Number")
                            .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Do nothing here
                                }
                            })
                            .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    final android.support.v7.app.AlertDialog builderSingle = builder.create();
                    builderSingle.show();
                    //Override the handler
                    builderSingle.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetworkUtil.enforceNetworkConnection(getActivity())) {
                                if (isValidMobileNumber(itemMobileEditText.getText().toString().trim())) {
                                    viewmodel.addCustomerMobileNumber(itemMobileEditText.getText().toString().trim());
                                }else {
                                    Utils.showFailureToast(HomeFragment.this.getActivity(),
                                            "Mobile number not valid",
                                            Toast.LENGTH_LONG);

                                }
                            }

                            builderSingle.dismiss();
                        }
                    });

                }else if (which == 2){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    final View view = inflater.inflate(R.layout.webapp_mobile_dialog, null);

                    final EditText itemMobileEditText = view.findViewById(R.id.customerMobile);
                    itemMobileEditText.setHint("Enter Mobile Number");

                    builder.setView(view);

                    builder.setMessage("Enter customer mobile number to view")
                            .setPositiveButton(R.string.view, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Utils.showFailureToast(HomeFragment.this.getActivity(),
                                            "Mobile number not valid",
                                            Toast.LENGTH_LONG);
                                    //Do nothing here

                                }
                            })
                            .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    final android.support.v7.app.AlertDialog builderSingle = builder.create();
                    builderSingle.show();
                    //Override the handler
                    builderSingle.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //update invoice item
                            builderSingle.dismiss();
                            if (isValidMobileNumber(itemMobileEditText.getText().toString().trim())) {
                                String url = AppConstants.WEB_APP_URL_1 + itemMobileEditText.getText().toString().trim() + AppConstants.WEB_APP_URL_2;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }else {
                                Utils.showFailureToast(HomeFragment.this.getActivity(),
                                        "Mobile number not valid",
                                        Toast.LENGTH_LONG);

                            }
                        }
                    });

                }else if (which ==3){
                    viewmodel.processSettlement();
                }else if (which ==4){
                    openWarningDialogMerchantReset();
                }else if (which ==5){
                    Log.d("CSD","Activate Merchant Clicked");
                    startActivity(new Intent(getContext(), ActivateMerchantActivity.class));
                }

                        }
        });
        builderSingle.show();
    }
   /* private void openDialogMerchantReset() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.reset_merchant_dialog, null);

        final EditText itemMobileEditText = view.findViewById(R.id.customerMobile);
        itemMobileEditText.setHint("Enter Mobile Number");

        builder.setView(view);

        builder.setMessage("Enter merchant mobile number to reset")
                .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here

                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final android.support.v7.app.AlertDialog builderSingle = builder.create();
        builderSingle.show();
        //Override the handler
        builderSingle.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update invoice item
                builderSingle.dismiss();
                if (isValidMobileNumber(itemMobileEditText.getText().toString().trim())) {
                    openWarningDialogMerchantReset();
                }else {
                    Utils.showFailureToast(HomeFragment.this.getActivity(),
                            "Mobile number not valid",
                            Toast.LENGTH_LONG);

                }
            }
        });
    }*/


    private void openWarningDialogMerchantReset() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Are you sure you wanted to reset this merchant");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewmodel.resetMerchant();
            }
        });
        builderSingle.show();
    }

    private void setupApiCallObservers() {
        viewmodel.resetMerchantApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(HomeFragment.this.getActivity());
                    progressDialog.setMessage(HomeFragment.this.getActivity().getResources().getString(R.string.reset_merchant_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.resetMerchantApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.resetMerchantApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            viewmodel.resetMerchantApiCallMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.resetMerchantApiCallSuccess.setValue(false);

                }
            }
        });

        viewmodel.resetMerchantApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(HomeFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.resetMerchantApiCallError.setValue(false);
                }
            }
        });


        viewmodel.updateMobileApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(HomeFragment.this.getActivity());
                    progressDialog.setMessage(HomeFragment.this.getActivity().getResources().getString(R.string.update_customer_mobile_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.updateMobileApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.updateMobileApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            HomeFragment.this.getActivity().getResources().getString(R.string.customer_mobile_added),
                            Toast.LENGTH_LONG);
                    viewmodel.updateMobileApiCallSuccess.setValue(false);

                }
            }
        });

        viewmodel.updateMobileApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(HomeFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.updateMobileApiCallError.setValue(false);
                }
            }
        });

        viewmodel.sendRemainderApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(HomeFragment.this.getActivity());
                    progressDialog.setMessage(HomeFragment.this.getActivity().getResources().getString(R.string.sending_remainder));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.sendRemainderApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.sendRemainderApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            viewmodel.apiCallSuccessMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.sendRemainderApiCallSuccess.setValue(false);

                }
            }
        });

        viewmodel.sendRemainderApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(HomeFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.sendRemainderApiCallError.setValue(false);
                }
            }
        });

        viewmodel.sendSettlementApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(HomeFragment.this.getActivity());
                    progressDialog.setMessage(HomeFragment.this.getActivity().getResources().getString(R.string.settlement_processing));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.sendSettlementApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.sendSettlementApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            viewmodel.apiCallSuccessMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.sendSettlementApiCallSuccess.setValue(false);

                }
            }
        });

        viewmodel.sendSettlementApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(HomeFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.sendSettlementApiCallSuccess.setValue(false);
                }
            }
        });
    }

    public boolean isValidMobileNumber(String input) {
        // starts with 6,7,8,9 and contains 10 digits total
        return input.matches("^[6789]\\d{9}$");
    }

}
