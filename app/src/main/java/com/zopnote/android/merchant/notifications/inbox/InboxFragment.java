package com.zopnote.android.merchant.notifications.inbox;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.InboxFragBinding;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerFragment;


/**
 * Created by salah on 30/01/2020.
 */

public class InboxFragment extends Fragment {

    private InboxFragBinding binding;
    private InboxViewModel viewmodel;
    private InboxAdapter adapter;
    private ProgressDialog progressDialog;

    public InboxFragment() {
        // Requires empty public constructor
    }

    public static InboxFragment newInstance() {
        InboxFragment fragment = new InboxFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = InboxFragBinding.inflate(inflater, container, false);

        binding.recyclerView.setHasFixedSize(true);

        adapter = new InboxAdapter(getActivity());

        RecyclerView.ItemDecoration
                dividerDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerDecoration);
        binding.recyclerView.setAdapter(adapter);

        // hide all
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        viewmodel = InboxActivity.obtainViewModel(getActivity());
        adapter.setViewModel(viewmodel);

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                getNotification();

                //to avoid calling api without merchant Id, also avoid multiple calls due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });

        binding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNotification();
            }
        });

        binding.clearAllNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotificationConfirmationDialog();
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
                    binding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    if(viewmodel.notifications != null && viewmodel.notifications.size() != 0){
                        adapter.setItems(viewmodel.notifications);
                        adapter.notifyDataSetChanged();
                        setStatusReady();
                    }else{
                        setStatusEmpty();
                    }

                    viewmodel.apiCallSuccess.setValue(false);
                }
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
                    binding.networkErrorView.networkErrorText.setText(viewmodel.apiCallErrorMessage);
                    setStatusNetworkError();
                    viewmodel.apiCallError.setValue(false);
                }
            }
        });

        viewmodel.apiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    if(viewmodel.notifications != null && viewmodel.notifications.size() != 0){
                        adapter.setItems(viewmodel.notifications);
                        adapter.notifyDataSetChanged();
                        setStatusReady();
                    }else{
                        setStatusEmpty();
                    }

                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });

        viewmodel.deleteNotificationApiRunning.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(InboxFragment.this.getActivity());
                    progressDialog.setMessage(InboxFragment.this.getActivity().getResources().getString(R.string.delete_notification_api_running_message));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.deleteNotificationApiRunning.setValue(false);
                    }
                }
            }
        });

        viewmodel.deleteNotificationApiCallSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    getNotification();
                    viewmodel.deleteNotificationApiCallSuccess.setValue(false);

                }
            }
        });

        viewmodel.deleteNotificationApiCallError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(InboxFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.deleteNotificationApiCallError.setValue(false);
                }
            }
        });




    }


    public void getNotification() {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getNotification();
        }else{
            viewmodel.networkError.postValue(true);
            binding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }
    private void setStatusNetworkError() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.VISIBLE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusLoading() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        binding.contentView.setVisibility(View.VISIBLE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }


    private void deleteNotificationConfirmationDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.clear_all_notification_warning))
                .setPositiveButton(R.string.button_yes_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.enforceConnection(getActivity())) {

                    return;
                }
                viewmodel.deleteNotification(null,true);
                dialog.dismiss();
            }
        });
    }


}
