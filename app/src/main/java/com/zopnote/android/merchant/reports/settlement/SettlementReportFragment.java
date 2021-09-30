package com.zopnote.android.merchant.reports.settlement;


import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.SettlementInfo;
import com.zopnote.android.merchant.databinding.SettlementReportFragBinding;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NetworkUtil;
import com.zopnote.android.merchant.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SettlementReportFragment extends Fragment {
    private SettlementReportFragBinding binding;
    private SettlementReportAdapter adapter;
    private SettlementReportViewModel viewmodel;
    private ProgressDialog progressDialog;
    private Double readyForSettlement  =0.00;
    //private String merchantType="Both";
    public SettlementReportFragment() {
        // Required empty public constructor
    }

    public static SettlementReportFragment newInstance() {
        SettlementReportFragment fragment = new SettlementReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = SettlementReportFragBinding.inflate(getLayoutInflater(), container, false);
        adapter = new SettlementReportAdapter(getActivity());

        binding.settlementDetailsLayout.recyclerView.setAdapter(adapter);

        // hide all
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
        binding.networkErrorView.getRoot().setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setStatusLoading();

        viewmodel = SettlementReportActivity.obtainViewModel(getActivity());

        viewmodel.merchant.observe(this, new Observer<Merchant>() {
            @Override
            public void onChanged(@Nullable Merchant merchant) {
                viewmodel.merchantId = merchant.getId();

                getReports();

                //to avoid calling api without merchant Id, also avoid multiple calls due to observer
                viewmodel.merchant.removeObserver(this);
            }
        });

        viewmodel.dateChanged.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean dateChanged) {
                if(dateChanged) {
                    if (viewmodel.merchantId != null) {
                        getReports();
                    }
                }
            }
        });

        binding.reqAdvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRequestAdvanceDialog();

            }
        });
        binding.amountBreakupLayout.transferAdvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettleNowConfirmation();

            }
        });
        binding.networkErrorView.networkErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReports();
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
                    if(viewmodel.settlementReport != null){
                        setData();
                        setStatusReady();
                    }else{
                        setStatusEmpty();
                    }

                    viewmodel.apiCallSuccess.setValue(false);
                }
            }
        });


        viewmodel.apiCallRunningReqAdv.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(SettlementReportFragment.this.getActivity());
                    progressDialog.setMessage(SettlementReportFragment.this.getActivity().getResources().getString(R.string.request_advance));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.apiCallRunningReqAdv.setValue(false);
                    }
                }
            }
        });

        viewmodel.apiCallSuccessReqAdv.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    Utils.showSuccessToast(getActivity(),
                            SettlementReportFragment.this.getActivity().getResources().getString(R.string.requested_advance),
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallSuccessReqAdv.setValue(false);

                }
            }
        });

        viewmodel.apiCallErrorReqAdv.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(SettlementReportFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallErrorReqAdv.setValue(false);
                }
            }
        });



        viewmodel.apiCallRunningSettleNow.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean running) {
                if (running) {
                    progressDialog = new ProgressDialog(SettlementReportFragment.this.getActivity());
                    progressDialog.setMessage(SettlementReportFragment.this.getActivity().getResources().getString(R.string.settle_now));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        viewmodel.apiCallRunningSettleNow.setValue(false);
                    }
                }
            }
        });

        viewmodel.apiCallSuccessSettleNow.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean success) {
                if (success) {
                    transferAdvContactDialog();
                    viewmodel.apiCallSuccessSettleNow.setValue(false);

                }
            }
        });

        viewmodel.apiCallErrorSettleNow.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean error) {
                if (error) {
                    Utils.showFailureToast(SettlementReportFragment.this.getActivity(),
                            viewmodel.apiCallErrorMessage,
                            Toast.LENGTH_LONG);
                    viewmodel.apiCallErrorSettleNow.setValue(false);
                }
            }
        });

    }
    private void transferAdvContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(viewmodel.settleNowMessage)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing here
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Override the handler
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void openSettleNowConfirmation () {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getResources().getString(R.string.settle_now_confirm),String.format("%.2f",readyForSettlement)))
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
                viewmodel.settleNow();
                dialog.dismiss();
            }
        });
    }

    private void openRequestAdvanceDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.req_adv_enter_amount_dialog, null);

        final EditText itemMobileEditText = view.findViewById(R.id.reqAdvAmount);
        itemMobileEditText.setHint(getString(R.string.enter_amount_label));

        builder.setView(view);

        builder.setMessage(getString(R.string.request_advance_label))
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
                    if (!itemMobileEditText.getText().toString().trim().isEmpty()) {
                        viewmodel.advanceAmount= itemMobileEditText.getText().toString().trim();
                        viewmodel.requestAdvance();
                    }else {
                        itemMobileEditText.setError(getResources().getString(R.string.enter_amount_label));
                    }
                }

                builderSingle.dismiss();
            }
        });
    }

    public void getReports() {

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            viewmodel.getSettlementReport();
        }else{
            viewmodel.networkError.postValue(true);
            binding.networkErrorView.networkErrorText.setText(R.string.no_network_error);
            setStatusNetworkError();
        }
    }

    private void setData() {
        binding.billedAmount.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getBilled(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        binding.cashCollectionAmount.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getCashCollection(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        binding.totalAmountUnpaid.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getUnPaid(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        binding.onlineCollectionAmount.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getOnlineCollection(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        binding.previousBalance.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getPreviousBalance(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        binding.currentBilling.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getBilled()-viewmodel.settlementReport.getPreviousBalance() , FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        if(BuildConfig.PRODUCT_FLAVOUR_MERCHANT == false) binding.reqAdvance.setVisibility(View.VISIBLE);
        else binding.reqAdvance.setVisibility(View.GONE);

        setupAmountBreakupLayout();

        setupSettlementDetails();

    }

    private void setupAmountBreakupLayout() {
        //Log.d("CSD","setupAmountBreakupLayout() : ");
        /*if (!selectedMonthIsCurrentMonth(viewmodel.month)) {
            //Log.d("CSD"," Inside If Condition ");
            binding.amountBreakupLayout.advanceTransferLayout.setVisibility(View.GONE);
            binding.amountBreakupLayout.pendingSettlementLayout.setVisibility(View.VISIBLE);
            binding.amountBreakupLayout.viewLastDivider.setVisibility(View.VISIBLE);
            binding.reqAdvance.setVisibility(View.GONE);
        } else{*/

            binding.amountBreakupLayout.pendingSettlementLayout.setVisibility(View.GONE);
            binding.amountBreakupLayout.viewLastDivider.setVisibility(View.GONE);

            Double totalToBeTransfer = viewmodel.settlementReport.getOnlineCollection()- viewmodel.settlementReport.getSettled();

            binding.amountBreakupLayout.pendingAmtSettled.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                    totalToBeTransfer, FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

             readyForSettlement = viewmodel.settlementReport.getAvailableAmount();

           List<SettlementInfo> settlementInfo =  viewmodel.settlementReport.getSettlements();
           if (settlementInfo.size()>0){
               if (isSettlementDateToday(settlementInfo.get(settlementInfo.size()-1).getDate()) ||
                       settlementInfo.get(settlementInfo.size()-1).getAmount() == readyForSettlement){
                   readyForSettlement =0.00;
               }
           }


            if (readyForSettlement<0) {
                String availableSettleAmount = "-"+FormatUtil.getRupeePrefixedAmount(getContext(),
                        Math.abs(readyForSettlement), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
                binding.amountBreakupLayout.availableSettledAmount.setText(availableSettleAmount);
            }else {
                binding.amountBreakupLayout.availableSettledAmount.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                        readyForSettlement, FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
            }
            if (readyForSettlement==0)
                binding.amountBreakupLayout.transferAdvance.setVisibility(View.GONE);

            binding.amountBreakupLayout.advanceTransferLayout.setVisibility(View.VISIBLE);
            if ( viewmodel.settlementReport.getAdvanceTransfer() !=0.00) {
                binding.amountBreakupLayout.advancePaidLayout.setVisibility(View.VISIBLE);
                binding.amountBreakupLayout.pendingTobeTransferLayout.setVisibility(View.VISIBLE);
                String advancePaid = "-" + FormatUtil.getRupeePrefixedAmount(getContext(),
                        viewmodel.settlementReport.getAdvanceTransfer(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
                binding.amountBreakupLayout.advancePaid.setText(advancePaid);

                Double pendingTobeTransfer = readyForSettlement - viewmodel.settlementReport.getAdvanceTransfer();

                if (pendingTobeTransfer<0) {
                    String pendingTobeTransferStr = "-"+FormatUtil.getRupeePrefixedAmount(getContext(),
                            Math.abs(pendingTobeTransfer), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
                    binding.amountBreakupLayout.pendingTobeTransfer.setText(pendingTobeTransferStr);
                }else {
                    binding.amountBreakupLayout.pendingTobeTransfer.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                            pendingTobeTransfer, FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
                }

            }else {
                binding.amountBreakupLayout.pendingTobeTransferLayout.setVisibility(View.GONE);
                binding.amountBreakupLayout.advancePaidLayout.setVisibility(View.GONE);
            }

        //}

        binding.amountBreakupLayout.settledAmount.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getSettled(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
        binding.amountBreakupLayout.settledAmountOD.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getSettled(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        LinearLayout defaultl= (LinearLayout) getView().findViewById(R.id.defaultTL);
        LinearLayout totNoOfInvProc= (LinearLayout) getView().findViewById(R.id.TotInvPro);
        LinearLayout totNoOfOrdProc= (LinearLayout) getView().findViewById(R.id.TotOrdPro);
        LinearLayout commissionl= (LinearLayout) getView().findViewById(R.id.commission);
        LinearLayout subsciptionl= (LinearLayout) getView().findViewById(R.id.subscription);
        LinearLayout divider= (LinearLayout) getView().findViewWithTag(R.id.divider2);

        if (viewmodel.settlementReport.getMerchantType() !=null && viewmodel.settlementReport.getMerchantType().equals("Subscription"))
        {
            defaultl.setVisibility(View.VISIBLE);
            totNoOfInvProc.setVisibility(View.VISIBLE);
            commissionl.setVisibility(View.VISIBLE);
            totNoOfOrdProc.setVisibility(View.GONE);
            subsciptionl.setVisibility(View.GONE);
        }
        else  if (viewmodel.settlementReport.getMerchantType() !=null && viewmodel.settlementReport.getMerchantType().equals("OnDemand"))
        {
            defaultl.setVisibility(View.VISIBLE);
            totNoOfOrdProc.setVisibility(View.VISIBLE);
            subsciptionl.setVisibility(View.VISIBLE);
            totNoOfInvProc.setVisibility(View.GONE);
            commissionl.setVisibility(View.GONE);
        }
        else if (viewmodel.settlementReport.getMerchantType() !=null && viewmodel.settlementReport.getMerchantType().equals("Both"))
        {
            defaultl.setVisibility(View.VISIBLE);
            totNoOfInvProc.setVisibility(View.VISIBLE);
            totNoOfOrdProc.setVisibility(View.VISIBLE);
            commissionl.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            subsciptionl.setVisibility(View.VISIBLE);
        }

        binding.amountBreakupLayout.totalNoOfInvoicesProcessed.setText(""+viewmodel.settlementReport.getTotalNoOfInvoicesProcessed());

        binding.amountBreakupLayout.totalNoOfOrdersProcessed.setText(""+viewmodel.settlementReport.getTotalNoOfInvoicesProcessed());

        String lessCharges = "-"+FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getCharges(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        binding.amountBreakupLayout.lessCharges.setText(lessCharges);


        String sub_lessCharges = "-"+FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getCharges(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        binding.amountBreakupLayout.subLessCharges.setText(sub_lessCharges);

       // binding.amountBreakupLayout.paidBankCharges.setText("(Paid Bank Charges, "+FormatUtil.getRupeePrefixedAmount(getContext(),
         //       viewmodel.settlementReport.getPaidBankCharges(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS)+")");

        binding.amountBreakupLayout.bankCharges.setText("-"+FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getPaidBankCharges(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));

        /*Double gst = viewmodel.settlementReport.getCgst() + viewmodel.settlementReport.getSgst();
        String formattedGst = "-"+FormatUtil.getRupeePrefixedAmount(getContext(),
                gst, FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);*/
        Double gst = viewmodel.settlementReport.getCgst() + viewmodel.settlementReport.getSgst();
        String formattedGst = "-"+FormatUtil.getRupeePrefixedAmount(getContext(),
                gst, FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);

        binding.amountBreakupLayout.gstCharges.setText(formattedGst);
        binding.amountBreakupLayout.gst.setText(formattedGst);


        binding.amountBreakupLayout.transferredAmount.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                viewmodel.settlementReport.getTransferred(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
        if (viewmodel.settlementReport.getPending()<0) {
            String pendingAmount = "-" + FormatUtil.getRupeePrefixedAmount(getContext(),
                    Math.abs(viewmodel.settlementReport.getPending()), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
            binding.amountBreakupLayout.pendingAmount.setText(pendingAmount);
        }else
            binding.amountBreakupLayout.pendingAmount.setText(FormatUtil.getRupeePrefixedAmount(getContext(),
                    viewmodel.settlementReport.getPending(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));
    }

    private boolean isSettlementDateToday(Date date) {

        if (date.equals(new Date())){
            return true;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();

        return date.equals(Calendar.AM);
    }

    private boolean selectedMonthIsCurrentMonth(int month) {
        return new Date().getMonth()==month;
    }




    private void setupSettlementDetails() {
        adapter.setItems(viewmodel.settlementReport.getSettlements());
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
}
