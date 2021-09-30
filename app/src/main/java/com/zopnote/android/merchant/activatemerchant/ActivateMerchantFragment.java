package com.zopnote.android.merchant.activatemerchant;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.ActivateMerchantFragBinding;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

public class ActivateMerchantFragment extends Fragment {

    private ActivateMerchantFragBinding ActivateMerchantFragBinding;
    private ActivateMerchantViewModel viewmodel;

    public ActivateMerchantFragment() {
        // Required empty public constructor
    }

    public static ActivateMerchantFragment newInstance() {
        ActivateMerchantFragment fragment = new ActivateMerchantFragment();
        Log.d("CSD","ACTIVATE MERCHANT FRAGMENT");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActivateMerchantFragBinding = ActivateMerchantFragBinding.inflate(inflater, container, false);

        return ActivateMerchantFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = ActivateMerchantActivity.obtainViewModel(getActivity());
        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();

                //radioGroupPeriodChanged(); //OBSERVE
               // getReports();

                // to avoid calling api without merchant Id, also avoid multiple calls due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });


        ActivateMerchantFragBinding.submitActivateMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    if (isValidMobileNumber(ActivateMerchantFragBinding.editTXTmerchantMobile.getText().toString().trim()))
                    {
                        viewmodel.mobileNo=ActivateMerchantFragBinding.editTXTmerchantMobile.getText().toString().trim();
                        getReports(ActivateMerchantFragBinding.merchantName.getText().toString().trim(),
                                ActivateMerchantFragBinding.merchantBusinessName.getText().toString().trim(),
                                ActivateMerchantFragBinding.editTXTmerchantMobile.getText().toString().trim());
                    }
                    else {
                        Utils.showFailureToast(ActivateMerchantFragment.this.getActivity(),
                                "Mobile number not valid",
                                Toast.LENGTH_LONG);
                    }
                   // viewmodel.area= String.valueOf(ActivateMerchantFragBinding.editTXTmerchantMobile.getText());

                    //viewmodel.addCustomer();
                }
            }
        });

        ActivateMerchantFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  getReports();
            }
        });

        viewmodel.apiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                    viewmodel.apiCallRunning.setValue(false);
                }
            }
        });

        viewmodel.apiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    ActivateMerchantFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallError.setValue(false);

                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    getContext().startActivity(intent);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {

                if (success) {
                    viewmodel.apiCallSuccess.setValue(false);
                    setStatusReady();
                }
                else
                {
                    setStatusEmpty();
                }
                Intent intent = new Intent(getContext(), HomeActivity.class);
                getContext().startActivity(intent);
            }
        });

    }
    public void getReports(String merchantName, String merchantBusinessName,String mobileNo) {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.callActivateMerchantAPI(merchantName,merchantBusinessName,mobileNo);
        }else{
            viewmodel.networkError.postValue(true);
            ActivateMerchantFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void setStatusNetworkError () {
        ActivateMerchantFragBinding.contentView.setVisibility(View.GONE);
        ActivateMerchantFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        ActivateMerchantFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        ActivateMerchantFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading () {
       // ActivateMerchantFragBinding.contentView.setVisibility(View.VISIBLE);
        ActivateMerchantFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        ActivateMerchantFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ActivateMerchantFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusEmpty () {
        ActivateMerchantFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        ActivateMerchantFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ActivateMerchantFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady () {
        ActivateMerchantFragBinding.contentView.setVisibility(View.VISIBLE);
        ActivateMerchantFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        ActivateMerchantFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        ActivateMerchantFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

        public boolean isValidMobileNumber(String input) {
            // starts with 6,7,8,9 and contains 10 digits total
            return input.matches("^[6789]\\d{9}$");
        }

}