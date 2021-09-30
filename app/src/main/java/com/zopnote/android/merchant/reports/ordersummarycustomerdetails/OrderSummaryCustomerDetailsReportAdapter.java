package com.zopnote.android.merchant.reports.ordersummarycustomerdetails;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.OrderSummaryCustomer;
import com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsReportItemBinding;
import com.zopnote.android.merchant.invoice.InvoiceActivity;
import com.zopnote.android.merchant.reports.collection.CollectionActivity;
import com.zopnote.android.merchant.reports.collection.PaymentFilterOption;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderSummaryCustomerDetailsReportAdapter extends BaseRecyclerAdapter<OrderSummaryCustomer, OrderSummaryCustomerDetailsReportAdapter.OSRViewHolder> {
    private OrderSummaryCustomerDetailsReportViewModel viewmodel;

    public OrderSummaryCustomerDetailsReportAdapter (Context context) {
        super(context);
    }
    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<OrderSummaryCustomer> newItems) {
        OrderSummaryCustomer oldItem = getItems().get(oldItemPosition);
        OrderSummaryCustomer newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getInvoiceNumber(), newItem.getInvoiceNumber());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<OrderSummaryCustomer> newItems) {
        OrderSummaryCustomer oldItem = getItems().get(oldItemPosition);
        OrderSummaryCustomer newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getInvoiceNumber(), newItem.getInvoiceNumber()) &&
                ObjectsUtil.equals(oldItem.getInvoiceNumber(), newItem.getInvoiceNumber()) &&
                ObjectsUtil.equals(oldItem.getFirstName(), newItem.getFirstName());
    }
    @Override
    public OSRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OrderSummaryCustomerDetailsReportItemBinding binding= DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.order_summary_customer_details_report_item,
                        parent, false);
        final OrderSummaryCustomerDetailsReportAdapter.OSRViewHolder vh = new OrderSummaryCustomerDetailsReportAdapter.OSRViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(OSRViewHolder holder, int position) {
        final OrderSummaryCustomer OrderSummaryCustomer = getItems().get(position);

       // holder.binding.invoiceNumber.setText(viewmodel.customers.get(position).getInvoiceNumber());

        SpannableString content = new SpannableString(viewmodel.customers.get(position).getInvoiceNumber());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.binding.invoiceNumber.setText(content);

        long val = viewmodel.customers.get(position).getInvoiceDate();
        //String dateText =  new SimpleDateFormat("dd/MM/yyyy").format(new Date(val));
        String status=viewmodel.customers.get(position).getStatus();

        if ((status.equals("OPEN")) || (status.equals("UNPAID")) || (status.equals("EXPIRED")) || (status.equals("DRAFT")))  {
            status = "UNPAID";
            ((TextView)holder.binding.status.findViewById(R.id.status)).setTextColor(ContextCompat.getColor(getContext(), R.color.warning_red));
        }
        else if(status.equals("PAID")) {
            ((TextView) holder.binding.status.findViewById(R.id.status)).setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }
        else {
            ((TextView) holder.binding.status.findViewById(R.id.status)).setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        }


        long val1 = viewmodel.customers.get(position).getInvoiceDate();
        String dateText =  new SimpleDateFormat("dd MMM").format(new Date(val1));

        holder.binding.invoiceDate.setText(dateText);

        holder.binding.status.setText(status);
        //holder.binding.invoiceAmount.setText(viewmodel.customers.get(position).getInvoiceAmount());
        String invoiceAmount = FormatUtil.getRupeePrefixedAmount(getContext(),
                Double.valueOf(viewmodel.customers.get(position).getInvoiceAmount()),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);

        holder.binding.invoiceAmount.setText(invoiceAmount);


        holder.binding.invoiceNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewBill(OrderSummaryCustomer.getCustomerId(),OrderSummaryCustomer.getInvoiceId());
            }
        });
    }

    private void viewBill(String customerId,String invoiceId)
    {
        Intent intent = new Intent(getContext(), InvoiceActivity.class);
        intent.putExtra(Extras.CUSTOMER_ID, customerId);
        intent.putExtra(Extras.INVOICE_ID, invoiceId);

        Log.d("CSD",customerId+"----"+invoiceId);
        getContext().startActivity(intent);
    }

    public class OSRViewHolder extends RecyclerView.ViewHolder {
        final OrderSummaryCustomerDetailsReportItemBinding binding;
           public OSRViewHolder(OrderSummaryCustomerDetailsReportItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
    }
    public void setViewModel(OrderSummaryCustomerDetailsReportViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }
}