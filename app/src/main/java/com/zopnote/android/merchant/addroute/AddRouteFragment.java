package com.zopnote.android.merchant.addroute;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.AddRouteFragBinding;
import com.zopnote.android.merchant.util.NetworkUtil;

import java.util.regex.Pattern;

public class AddRouteFragment extends Fragment {

    private AddRouteFragBinding addRouteFragBinding;
    private AddRouteViewModel viewmodel;

    public AddRouteFragment() {
        // Required empty public constructor
    }

    public static AddRouteFragment newInstance() {
        AddRouteFragment fragment = new AddRouteFragment();
        Log.d("CSD", "ADD ROUTE FRAGMENT");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        addRouteFragBinding = AddRouteFragBinding.inflate(inflater, container, false);

        return addRouteFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddRouteActivity.obtainViewModel(getActivity());
        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();


                // to avoid calling api without merchant Id, also avoid multiple calls due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });


        addRouteFragBinding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.enforceNetworkConnection(getContext())) {
                    viewmodel.route = String.valueOf(addRouteFragBinding.editTXTaddRoute.getText());
                    addRouteValidate();

                }
            }
        });

        addRouteFragBinding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
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
                    addRouteFragBinding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
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

    public void addRouteValidate() {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {

            if (!addRouteFragBinding.editTXTaddRoute.getText().toString().trim().isEmpty()) {
                if (addRouteFragBinding.editTXTaddRoute.getText().toString().trim().length() <= 20) {
                    if (isSpecialCharFound(addRouteFragBinding.editTXTaddRoute.getText().toString().trim())) {
                        addRouteFragBinding.editTXTaddRoute.setError(getResources().getString(R.string.special_char_not_allowed));
                        addRouteFragBinding.editTXTaddRoute.requestFocus();
                    } else {
                        viewmodel.addRouteInfo();
                    }

                } else {
                    addRouteFragBinding.editTXTaddRoute.setError(getResources().getString(R.string.not_more_than_20_char));
                    addRouteFragBinding.editTXTaddRoute.requestFocus();
                }

            } else {
                addRouteFragBinding.editTXTaddRoute.setError(getResources().getString(R.string.enter_route_label));
                addRouteFragBinding.editTXTaddRoute.requestFocus();
            }


        } else {
            viewmodel.networkError.postValue(true);
            addRouteFragBinding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void setStatusNetworkError() {
        addRouteFragBinding.contentView.setVisibility(View.GONE);
        addRouteFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        addRouteFragBinding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        addRouteFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        addRouteFragBinding.contentView.setVisibility(View.VISIBLE);
        addRouteFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        addRouteFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        addRouteFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusEmpty() {
        addRouteFragBinding.emptyView.getRoot().setVisibility(View.VISIBLE);
        addRouteFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        addRouteFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        addRouteFragBinding.contentView.setVisibility(View.VISIBLE);
        addRouteFragBinding.emptyView.getRoot().setVisibility(View.GONE);
        addRouteFragBinding.networkErrorView.getRoot().setVisibility(View.GONE);
        addRouteFragBinding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private boolean isSpecialCharFound(String str) {
        Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
        if (regex.matcher(str).find()) {
            // Log.d("TTT, "SPECIAL CHARS FOUND");
            return true;
        }
        return false;
    }

}