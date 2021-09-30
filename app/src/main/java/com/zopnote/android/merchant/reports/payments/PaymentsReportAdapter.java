package com.zopnote.android.merchant.reports.payments;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.PaymentReportItemBinding;
import com.zopnote.android.merchant.reports.collection.CollectionActivity;
import com.zopnote.android.merchant.reports.collection.PaymentFilterOption;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.util.List;

public class PaymentsReportAdapter extends BaseRecyclerAdapter<ReportItem, PaymentsReportAdapter.ReportViewHolder> {
    private PaymentsViewModel viewmodel;

    public PaymentsReportAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<ReportItem> newItems) {
        ReportItem oldItem = getItems().get(oldItemPosition);
        ReportItem newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getRoute(), newItem.getRoute());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<ReportItem> newItems) {
        ReportItem oldItem = getItems().get(oldItemPosition);
        ReportItem newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getRoute(), newItem.getRoute())
                && ObjectsUtil.equals(oldItem.getTotalAmountBilled(), newItem.getTotalAmountBilled())
                && ObjectsUtil.equals(oldItem.getTotalAmountPaidOnline(), newItem.getTotalAmountPaidOnline())
                && ObjectsUtil.equals(oldItem.getTotalAmountPaidCash(), newItem.getTotalAmountPaidCash())
                && ObjectsUtil.equals(oldItem.getTotalAmountPaidCheque(), newItem.getTotalAmountPaidCheque())
                && ObjectsUtil.equals(oldItem.getTotalAmountPaidGPay(), newItem.getTotalAmountPaidGPay())
                && ObjectsUtil.equals(oldItem.getTotalAmountPaidPaytm(), newItem.getTotalAmountPaidPaytm())
                && ObjectsUtil.equals(oldItem.getTotalAmountPaidPhonepe(), newItem.getTotalAmountPaidPhonepe())
                && ObjectsUtil.equals(oldItem.getTotalAmountPaidUPI(), newItem.getTotalAmountPaidUPI())
                && ObjectsUtil.equals(oldItem.getTotalAmountPaidOther(), newItem.getTotalAmountPaidOther())

                && ObjectsUtil.equals(oldItem.getTotalAmountUnpaid(), newItem.getTotalAmountUnpaid())
                && ObjectsUtil.equals(oldItem.getTotalNumberBilled(), newItem.getTotalNumberBilled())
                && ObjectsUtil.equals(oldItem.getTotalNumberPaidOnline(), newItem.getTotalNumberPaidOnline())
                && ObjectsUtil.equals(oldItem.getTotalNumberPaidCash(), newItem.getTotalNumberPaidCash())
                && ObjectsUtil.equals(oldItem.getTotalNumberPaidCheque(), newItem.getTotalNumberPaidCheque())
                && ObjectsUtil.equals(oldItem.getTotalNumberPaidGPay(), newItem.getTotalNumberPaidGPay())
                && ObjectsUtil.equals(oldItem.getTotalNumberPaidPaytm(), newItem.getTotalNumberPaidPaytm())
                && ObjectsUtil.equals(oldItem.getTotalNumberPaidPhonepe(), newItem.getTotalNumberPaidPhonepe())
                && ObjectsUtil.equals(oldItem.getTotalNumberPaidUPI(), newItem.getTotalNumberPaidUPI())
                && ObjectsUtil.equals(oldItem.getTotalNumberPaidOther(), newItem.getTotalNumberPaidOther())
                && ObjectsUtil.equals(oldItem.getTotalNumberUnpaid(), newItem.getTotalNumberUnpaid());
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PaymentReportItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.payment_report_item,
                        parent, false);
        final ReportViewHolder vh = new ReportViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ReportViewHolder holder, int position) {
        final ReportItem reportItem = getItems().get(position);

        holder.binding.route.setText(reportItem.getRoute());

        String totalAmountBilled = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountBilled(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountBilled.setText(totalAmountBilled);

        String totalAmountPaidOnline = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountPaidOnline(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountPaidOnline.setText(totalAmountPaidOnline);

        String totalAmountPaidCash = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountPaidCash(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountPaidCash.setText(totalAmountPaidCash);

        String totalAmountPaidCheque = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountPaidCheque(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountPaidCheque.setText(totalAmountPaidCheque);

        String totalAmountPaidGPay = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountPaidGPay(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountPaidGpay.setText(totalAmountPaidGPay);

        String totalAmountPaidPaytm = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountPaidPaytm(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountPaidPaytm.setText(totalAmountPaidPaytm);

        String totalAmountPaidPhonepe = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountPaidPhonepe(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountPaidPhonepe.setText(totalAmountPaidPhonepe);

        String totalAmountPaidUPI = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountPaidUPI(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountPaidUPI.setText(totalAmountPaidUPI);

        String totalAmountPaidOther = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountPaidOther(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountPaidOther.setText(totalAmountPaidOther);

        String totalAmountUnpaid = FormatUtil.getRupeePrefixedAmount(getContext(),
                reportItem.getTotalAmountUnpaid(),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        holder.binding.totalAmountUnpaid.setText(totalAmountUnpaid);
        if (reportItem.getTotalAmountUnpaid().compareTo(0d) == 0) {
            holder.binding.totalAmountUnpaid.setTextColor(getContext().getResources().getColor(R.color.text_primary));
        } else {
            holder.binding.totalAmountUnpaid.setTextColor(getContext().getResources().getColor(R.color.warning_red));
        }

        if (reportItem.getTotalNumberBilled() != 0)
        {
            int paidOnlinePercentage= (int) roundOff((reportItem.getTotalNumberPaidOnline()*100.0)/reportItem.getTotalNumberBilled(),0);
            holder.binding.totalNumberPaidOnlinePercentage.setText(String.valueOf(paidOnlinePercentage+
                    getContext().getResources().getString(R.string.percentage_symbol)));
            /* Payment Options */
            int paidPercentage;
            if(reportItem.getTotalNumberPaidCash()>0) {
                paidPercentage= (int) roundOff((reportItem.getTotalNumberPaidCash()*100.0)/reportItem.getTotalNumberBilled(),0);

                holder.binding.paidCashLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidCashPercentage.setText(String.valueOf(paidPercentage +
                        getContext().getResources().getString(R.string.percentage_symbol)));
                holder.binding.cashDiv.setVisibility(View.VISIBLE);
            }
            if(reportItem.getTotalNumberPaidCheque()>0) {
                paidPercentage= (int) roundOff((reportItem.getTotalNumberPaidCheque()*100.0)/reportItem.getTotalNumberBilled(),0);
                holder.binding.paidChequeLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidChequePercentage.setText(String.valueOf(paidPercentage +
                        getContext().getResources().getString(R.string.percentage_symbol)));
                holder.binding.chequeDiv.setVisibility(View.VISIBLE);
            }
            if(reportItem.getTotalNumberPaidGPay()>0) {
                paidPercentage= (int) roundOff((reportItem.getTotalNumberPaidGPay()*100.0)/reportItem.getTotalNumberBilled(),0);
                holder.binding.paidGPayLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidGPayPercentage.setText(String.valueOf(paidPercentage +
                        getContext().getResources().getString(R.string.percentage_symbol)));
                holder.binding.GpayDiv.setVisibility(View.VISIBLE);
            }
            if(reportItem.getTotalNumberPaidPaytm()>0) {
                paidPercentage= (int) roundOff((reportItem.getTotalNumberPaidPaytm()*100.0)/reportItem.getTotalNumberBilled(),0);
                holder.binding.paidPaytmLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidPaytmPercentage.setText(String.valueOf(paidPercentage +
                        getContext().getResources().getString(R.string.percentage_symbol)));
                holder.binding.paytmDiv.setVisibility(View.VISIBLE);
            }
            if(reportItem.getTotalNumberPaidPhonepe()>0) {
                paidPercentage= (int) roundOff((reportItem.getTotalNumberPaidPhonepe()*100.0)/reportItem.getTotalNumberBilled(),0);
                holder.binding.paidPhonepeLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidPhonepePercentage.setText(String.valueOf(paidPercentage +
                        getContext().getResources().getString(R.string.percentage_symbol)));
                holder.binding.phonepeDiv.setVisibility(View.VISIBLE);
            }
            if(reportItem.getTotalNumberPaidUPI()>0) {
                paidPercentage= (int) roundOff((reportItem.getTotalNumberPaidUPI()*100.)/reportItem.getTotalNumberBilled(),0);
                holder.binding.paidUPILayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidUPIPercentage.setText(String.valueOf(paidPercentage +
                        getContext().getResources().getString(R.string.percentage_symbol)));
                holder.binding.upiDiv.setVisibility(View.VISIBLE);
            }
            if(reportItem.getTotalNumberPaidOther()>0) {
                paidPercentage= (int) roundOff((reportItem.getTotalNumberPaidOther()*100.0)/reportItem.getTotalNumberBilled(),0);

                holder.binding.paidOtherLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidOtherPercentage.setText(String.valueOf(paidPercentage +
                        getContext().getResources().getString(R.string.percentage_symbol)));
                holder.binding.otherDiv.setVisibility(View.VISIBLE);
            }
            /* Payment Options */
            int unPaidPercentage= (int) roundOff((reportItem.getTotalNumberUnpaid()*100.0)/reportItem.getTotalNumberBilled(),0);

            holder.binding.totalNumberUnpaidPercentage.setText(String.valueOf(unPaidPercentage+
                    getContext().getResources().getString(R.string.percentage_symbol)));
        }else {
            holder.binding.totalNumberPaidOnlinePercentage.setText(String.valueOf(0+getContext().getResources().getString(R.string.percentage_symbol)));
            /*if(reportItem.getTotalNumberPaidCash()>0) {
                holder.binding.paidCashLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidCashPercentage.setText(String.valueOf(0+
                        getContext().getResources().getString(R.string.percentage_symbol)));
            }
            if(reportItem.getTotalNumberPaidCheque()>0) {
                holder.binding.paidChequeLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidChequePercentage.setText(String.valueOf(0+
                        getContext().getResources().getString(R.string.percentage_symbol)));
            }
            if(reportItem.getTotalNumberPaidGPay()>0) {
                holder.binding.paidGPayLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidGPayPercentage.setText(String.valueOf(0+
                        getContext().getResources().getString(R.string.percentage_symbol)));
            }
            if(reportItem.getTotalNumberPaidPaytm()>0) {
                holder.binding.paidPaytmLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidPaytmPercentage.setText(String.valueOf(0+
                        getContext().getResources().getString(R.string.percentage_symbol)));
            }
            if(reportItem.getTotalNumberPaidPhonepe()>0) {
                holder.binding.paidPhonepeLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidPhonepePercentage.setText(String.valueOf(0+
                        getContext().getResources().getString(R.string.percentage_symbol)));
            }
            if(reportItem.getTotalNumberPaidUPI()>0) {
                holder.binding.paidUPILayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidUPIPercentage.setText(String.valueOf(0+
                        getContext().getResources().getString(R.string.percentage_symbol)));
            }
            if(reportItem.getTotalNumberPaidOther()>0) {
                holder.binding.paidOtherLayout.setVisibility(View.VISIBLE);
                holder.binding.totalNumberPaidOtherPercentage.setText(String.valueOf(0+
                        getContext().getResources().getString(R.string.percentage_symbol)));
            }*/
            holder.binding.totalNumberUnpaidPercentage.setText(String.valueOf(0+
                    getContext().getResources().getString(R.string.percentage_symbol)));
        }



        holder.binding.totalNumberBilled.setText(String.valueOf(reportItem.getTotalNumberBilled()));
        holder.binding.totalNumberPaidOnline.setText(String.valueOf(reportItem.getTotalNumberPaidOnline()));
        holder.binding.totalNumberPaidCash.setText(String.valueOf(reportItem.getTotalNumberPaidCash()));
        holder.binding.totalNumberPaidCheque.setText(String.valueOf(reportItem.getTotalNumberPaidCheque()));
        holder.binding.totalNumberPaidGPay.setText(String.valueOf(reportItem.getTotalNumberPaidGPay()));
        holder.binding.totalNumberPaidPaytm.setText(String.valueOf(reportItem.getTotalNumberPaidPaytm()));
        holder.binding.totalNumberPaidPhonepe.setText(String.valueOf(reportItem.getTotalNumberPaidPhonepe()));
        holder.binding.totalNumberPaidUPI.setText(String.valueOf(reportItem.getTotalNumberPaidUPI()));
        holder.binding.totalNumberPaidOther.setText(String.valueOf(reportItem.getTotalNumberPaidOther()));
        holder.binding.totalNumberUnpaid.setText(String.valueOf(reportItem.getTotalNumberUnpaid()));

        holder.binding.billedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.BILLED);
            }
        });

        holder.binding.paidCashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PAID_CASH);
            }
        });

        holder.binding.paidChequeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PAID_CHEQUE);
            }
        });

        holder.binding.paidGPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PAID_GPAY);
            }
        });

        holder.binding.paidPaytmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PAID_PAYTM);
            }
        });

        holder.binding.paidPhonepeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PAID_PHONEPE);
            }
        });

        holder.binding.paidUPILayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PAID_UPI);
            }
        });

        holder.binding.paidOtherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PAID_OTHER);
            }
        });

        holder.binding.paidOnlineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PAID_ONLINE);
            }
        });

        holder.binding.pendingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCollectionReport(reportItem.getRoute(), PaymentFilterOption.PENDING);
            }
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private double roundOff (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    private void openCollectionReport(String route, PaymentFilterOption filterOption) {
        Intent intent = new Intent(getContext(), CollectionActivity.class);
        intent.putExtra(Extras.ROUTE, route);
        intent.putExtra(Extras.FILTER_TYPE, filterOption.name());
        intent.putExtra(Extras.MONTH, viewmodel.month);
        intent.putExtra(Extras.YEAR, viewmodel.year);
        getContext().startActivity(intent);
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

        final PaymentReportItemBinding binding;

        public ReportViewHolder(PaymentReportItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setViewmodel(PaymentsViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }
}
