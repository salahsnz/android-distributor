package com.zopnote.android.merchant.merchantsetup;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.MerchantBankInfoFragBinding;
import com.zopnote.android.merchant.home.HomeActivity;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

public class MerchantBankInfoFragment extends Fragment {

    private MerchantBankInfoFragBinding merchantBankInfoFragBinding;
    private MerchantBankInfoViewModel viewmodel;

    public MerchantBankInfoFragment() {
        // Required empty public constructor
    }

    public static MerchantBankInfoFragment newInstance() {
        MerchantBankInfoFragment fragment = new MerchantBankInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        merchantBankInfoFragBinding = merchantBankInfoFragBinding.inflate(inflater, container, false);

        return merchantBankInfoFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();
        String StepTitle1;
        StepTitle1=getResources().getString(R.string.merchant_setup_bank_info_step_profile);

        merchantBankInfoFragBinding.stepTitle.setText(StepTitle1);

        setStatusLoading();

        viewmodel = MerchantBankInfoActivity.obtainViewModel(getActivity());
        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                getBankInfoData();
                viewmodel.merchant.removeObserver(this);
            }
        });


        merchantBankInfoFragBinding.submitBankInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAllFields()) {
                    if (NetworkUtil.enforceNetworkConnection(getContext())) {
                        getReports();
                        //startActivity(new Intent(getContext(), MerchantKYCActivity.class));
                    }
                }
            }
        });

        merchantBankInfoFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  getReports();
            }
        });

        merchantBankInfoFragBinding.closeBankInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getResources()!= null)
                    getActivity().finish();

                startActivity(new Intent(getContext(), HomeActivity.class));
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
                    merchantBankInfoFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    if(viewmodel.merchantModel != null){
                        setData();
                        setStatusReady();
                    }else{
                        setStatusEmpty();
                    }
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });

        viewmodel.addMerchantApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            getActivity().getResources().getString(R.string.bank_updated_success_message),
                            Toast.LENGTH_LONG);
                    //Prefs.putBoolean(AppConstants.PREFS_ACCEPT_MERCHANT_AGREMENT, true);
                    //getActivity().finish();
                    startActivity(new Intent(getContext(), HomeActivity.class));

                }
            }
        });

        viewmodel.addMerchantApiCallRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    setStatusLoading();
                    viewmodel.addMerchantApiCallRunning.setValue(false);
                }
            }
        });

        viewmodel.addMerchantApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                }
            }
        });
    }

    private boolean validateAllFields() {
        if(validate(merchantBankInfoFragBinding.accountName) && validate(merchantBankInfoFragBinding.accountNo) &&
                validate(merchantBankInfoFragBinding.ifscCode) && validate(merchantBankInfoFragBinding.bankName)){
            return true;
        }
        return false;
    }

    public boolean validate(EditText editText) {
        if(editText.equals(merchantBankInfoFragBinding.accountName)){
            if (merchantBankInfoFragBinding.accountName.getText().toString().trim().length() > 2) {
                viewmodel.merchantBankAccountName=merchantBankInfoFragBinding.accountName.getText().toString();
                return true;
            } else {
                merchantBankInfoFragBinding.accountName.setError(getResources().getString(R.string.bank_account_name_error_message));
                merchantBankInfoFragBinding.accountName.requestFocus();
                return false;
            }
        }

        if(editText.equals(merchantBankInfoFragBinding.accountNo)){
            if (merchantBankInfoFragBinding.accountNo.getText().toString().trim().length() > 10)   {
                viewmodel.merchantBankAccountNo=merchantBankInfoFragBinding.accountNo.getText().toString();
                return true;
            } else {
                merchantBankInfoFragBinding.accountNo.setError(getResources().getString(R.string.bank_account_no_error_message));
                merchantBankInfoFragBinding.accountNo.requestFocus();
                return false;
            }
        }

        if(editText.equals(merchantBankInfoFragBinding.ifscCode)){
            if (merchantBankInfoFragBinding.ifscCode.getText().toString().trim().length() == 11) {
                viewmodel.merchantBankIFSCCode=merchantBankInfoFragBinding.ifscCode.getText().toString();
                return true;
            } else {
                merchantBankInfoFragBinding.ifscCode.setError(getResources().getString(R.string.bank_ifsc_code_error_message));
                merchantBankInfoFragBinding.ifscCode.requestFocus();
                return false;
            }
        }

        if(editText.equals(merchantBankInfoFragBinding.bankName)){
            if (merchantBankInfoFragBinding.bankName.getText().toString().trim().length() > 0) {
                viewmodel.merchantBankName=merchantBankInfoFragBinding.bankName.getText().toString();
                return true;
            } else {
                merchantBankInfoFragBinding.bankName.setError(getResources().getString(R.string.bank_name_error_message));
                merchantBankInfoFragBinding.bankName.requestFocus();
                return false;
            }
        }
        return false;
    }

    private void setData() {
        merchantBankInfoFragBinding.accountName.setText(viewmodel.merchantModel.getBankAccountName());
        merchantBankInfoFragBinding.accountNo.setText(viewmodel.merchantModel.getBankAccountNo());
        merchantBankInfoFragBinding.ifscCode.setText(viewmodel.merchantModel.getBankIFSCCode());
        merchantBankInfoFragBinding.bankName.setText(viewmodel.merchantModel.getBankName());
    }

    private void getBankInfoData()
    {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getBankInfoData();
        }
        else{
            viewmodel.networkError.postValue(true);
            merchantBankInfoFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    public void getReports() {
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.submitBankInfo();
            //viewmodel.getMerchantRoutes();
        }else{
            viewmodel.networkError.postValue(true);
            merchantBankInfoFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void setStatusNetworkError () {
       // merchantBankInfoFragBinding.contentView.setVisibility(View.GONE);
      //  merchantBankInfoFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        merchantBankInfoFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        merchantBankInfoFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading () {
      //  MerchantBankInfoFragBinding.contentView.setVisibility(View.VISIBLE);
      //  MerchantBankInfoFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        merchantBankInfoFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantBankInfoFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty () {
       // MerchantBankInfoFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        merchantBankInfoFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantBankInfoFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady () {
       // MerchantBankInfoFragBinding.contentView.setVisibility(View.VISIBLE);
        merchantBankInfoFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        merchantBankInfoFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

}