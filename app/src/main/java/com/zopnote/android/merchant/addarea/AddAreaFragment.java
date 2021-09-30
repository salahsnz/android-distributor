package com.zopnote.android.merchant.addarea;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.AddAreaFragBinding;
import com.zopnote.android.merchant.util.NetworkUtil;

import java.util.regex.Pattern;

public class AddAreaFragment extends Fragment {

    private AddAreaFragBinding addAreaFragBinding;

    private AddAreaViewModel viewmodel;

    public AddAreaFragment() {
        // Required empty public constructor
    }

    public static AddAreaFragment newInstance() {
        AddAreaFragment fragment = new AddAreaFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        addAreaFragBinding = AddAreaFragBinding.inflate(inflater, container, false);

        return addAreaFragBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddAreaActivity.obtainViewModel(getActivity());
        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();

                // to avoid calling api without merchant Id, also avoid multiple calls due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });


        addAreaFragBinding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    viewmodel.area = String.valueOf(addAreaFragBinding.editTXTaddArea.getText());

                    addAreaValidate();
                }
            }
        });

        addAreaFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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
                    addAreaFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {

                if (success) {
                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });

    }

    public void addAreaValidate() {


        if (NetworkUtil.isNetworkAvailable(getActivity())) {


            if (!addAreaFragBinding.editTXTaddArea.getText().toString().trim().isEmpty()) {
                if (addAreaFragBinding.editTXTaddArea.getText().toString().trim().length() <= 20) {
                    if (isSpecialCharFound(addAreaFragBinding.editTXTaddArea.getText().toString().trim())) {
                        addAreaFragBinding.editTXTaddArea.setError(getResources().getString(R.string.special_char_not_allowed));
                        addAreaFragBinding.editTXTaddArea.requestFocus();
                    } else {
                        viewmodel.addAreaInfo();
                    }

                } else {
                    addAreaFragBinding.editTXTaddArea.setError(getResources().getString(R.string.not_more_than_20_char));
                    addAreaFragBinding.editTXTaddArea.requestFocus();
                }

            } else {
                addAreaFragBinding.editTXTaddArea.setError(getResources().getString(R.string.enter_area_label));
                addAreaFragBinding.editTXTaddArea.requestFocus();
            }


        } else {
            viewmodel.networkError.postValue(true);
            addAreaFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private boolean isSpecialCharFound(String str) {
        Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
        if (regex.matcher(str).find()) {
            // Log.d("TTT, "SPECIAL CHARS FOUND");
            return true;
        }
        return false;
    }


    private void setStatusNetworkError() {
        addAreaFragBinding.contentView.setVisibility(View.GONE);
        addAreaFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        addAreaFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        addAreaFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        addAreaFragBinding.contentView.setVisibility(View.VISIBLE);
        addAreaFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        addAreaFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        addAreaFragBinding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty() {
        addAreaFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        addAreaFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        addAreaFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        addAreaFragBinding.contentView.setVisibility(View.VISIBLE);
        addAreaFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        addAreaFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        addAreaFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

}