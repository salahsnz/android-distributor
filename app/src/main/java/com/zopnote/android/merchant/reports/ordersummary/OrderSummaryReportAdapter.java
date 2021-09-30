package com.zopnote.android.merchant.reports.ordersummary;

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
import com.zopnote.android.merchant.databinding.OrderSummaryReportItemBinding;
import com.zopnote.android.merchant.invoice.InvoiceActivity;
import com.zopnote.android.merchant.reports.ordersummarycustomerdetails.OrderSummaryCustomerDetailsReportActivity;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderSummaryReportAdapter extends BaseRecyclerAdapter<OrderSummaryCustomer, OrderSummaryReportAdapter.OSRViewHolder> {
    private OrderSummaryReportViewModel viewmodel;

    public OrderSummaryReportAdapter(Context context) {
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
        OrderSummaryReportItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.order_summary_report_item,
                        parent, false);
        final OrderSummaryReportAdapter.OSRViewHolder vh = new OrderSummaryReportAdapter.OSRViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(OSRViewHolder holder, int position) {
        final OrderSummaryCustomer OrderSummaryCustomer = getItems().get(position);

        holder.binding.invoiceNumber.setText(viewmodel.customers.get(position).getInvoiceNumber());

        //TextView textView = (TextView) view.findViewById(R.id.textview);
        SpannableString content = new SpannableString(viewmodel.customers.get(position).getInvoiceNumber());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.binding.invoiceNumber.setText(content);

        long val = viewmodel.customers.get(position).getInvoiceDate();
        String dateText =  new SimpleDateFormat("dd MMM").format(new Date(val));
        //String dateText = new SimpleDateFormat("MM/dd/yyyy").format(new Date(val));


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

        holder.binding.invoiceDate.setText(dateText);
      //  holder.binding.doorNumber.setText(viewmodel.customers.get(position).getDoorNumber());


        SpannableString content1 = new SpannableString(viewmodel.customers.get(position).getDoorNumber());
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        holder.binding.doorNumber.setText(content1);

        String invoiceAmount = FormatUtil.getRupeePrefixedAmount(getContext(),
                Double.valueOf(viewmodel.customers.get(position).getInvoiceAmount()),
                FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);

        holder.binding.invoiceAmount.setText(invoiceAmount);
        holder.binding.status.setText(status);

        int radioSelected = viewmodel.selectedPeriodIndex;
        final String radioSelectedVal=viewmodel.selectedPeriod;

        final int finalRadioSelected = radioSelected;
        final long startDateSelected=viewmodel.startDate;
        final long endDateSelected=viewmodel.endDate;
        holder.binding.doorNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrderSummaryCustomerDetailsReport(OrderSummaryCustomer.getInvoiceNumber(),
                        OrderSummaryCustomer.getCustomerId(),
                        OrderSummaryCustomer.getMerchantId(),
                        OrderSummaryCustomer.getCustomerName(),
                        OrderSummaryCustomer.getCustomerStatus(),
                        OrderSummaryCustomer.getAddressLine1(),
                        OrderSummaryCustomer.getMobileNumber(),
                        OrderSummaryCustomer.getDoorNumber(),
                        OrderSummaryCustomer.getAddressLine2(),
                        OrderSummaryCustomer.getRoute(),
                        OrderSummaryCustomer.getEmail(),
                        finalRadioSelected,startDateSelected,endDateSelected,radioSelectedVal
                );
            }
        });


        holder.binding.invoiceNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewBill(OrderSummaryCustomer.getCustomerId(),OrderSummaryCustomer.getInvoiceId());
            }
        });

        /*String formattedAmount = FormatUtil.getRupeePrefixedAmount(getContext(), OrderSummaryCustomer.getInvoiceNumber(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        String formattedCharges = FormatUtil.getRupeePrefixedAmount(getContext(), OrderSummaryCustomer.getCharges(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS);
        String amountAndCharges = String.format("%s - %s", formattedAmount, formattedCharges);

        holder.binding.amountAndCharges.setText(amountAndCharges);
        holder.binding.invoiceNo.setText("11");
        holder.binding.transferred.setText(FormatUtil.getRupeePrefixedAmount(getContext(), orderSummaryReport.getTransferred(), FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS));*/
    }


    private void viewBill(String customerId,String invoiceId)
    {
        Intent intent = new Intent(getContext(), InvoiceActivity.class);
        intent.putExtra(Extras.CUSTOMER_ID, customerId);
        intent.putExtra(Extras.INVOICE_ID, invoiceId);
        getContext().startActivity(intent);
    }

    private void openOrderSummaryCustomerDetailsReport(String invoiceNo,String customerId,String merchantId,
                                                       String customerName, String customerStatus, String addressLine1,
                                                       String mobileNo, String doorNo,String addressLine2,String route,
                                                        String email,int radioSelectedIndex,long startDateSelected,
                                                       long endDateSelected,String radioSelectedVal)
    {
        Log.d("CSD","CLICKED  "+invoiceNo+" "+radioSelectedVal);
        Intent intent = new Intent(getContext(), OrderSummaryCustomerDetailsReportActivity.class);
        intent.putExtra(Extras.INVOICE_NO, invoiceNo);
        intent.putExtra(Extras.CUSTOMER_ID, customerId);
        intent.putExtra(Extras.MERCHANT_ID, merchantId);
        intent.putExtra(Extras.CUSTOMER_NAME, customerName);

        intent.putExtra(Extras.CUSTOMER_STATUS, customerStatus);
        intent.putExtra(Extras.MOBILE_NO, mobileNo);
        intent.putExtra(Extras.DOOR_NUMBER, doorNo);
        intent.putExtra(Extras.ADDRESS_LINE1, addressLine1);
        intent.putExtra(Extras.ADDRESS_LINE2, addressLine2);
        intent.putExtra(Extras.ROUTE, route);
        intent.putExtra(Extras.EMAIL, email);

        intent.putExtra(Extras.RADIO_SELECTED_INDEX, String.valueOf(radioSelectedIndex));
        intent.putExtra(Extras.START_DATE, String.valueOf(startDateSelected));
        intent.putExtra(Extras.END_DATE, String.valueOf(endDateSelected));
        intent.putExtra(Extras.RADIO_SELECTED, radioSelectedVal);

        //Log.d()

        /*intent.putExtra(Extras.FILTER_TYPE, filterOption.name());
        intent.putExtra(Extras.MONTH, viewmodel.month);
        intent.putExtra(Extras.YEAR, viewmodel.year);*/
        getContext().startActivity(intent);
    }

    public class OSRViewHolder extends RecyclerView.ViewHolder {
        final OrderSummaryReportItemBinding binding;
           public OSRViewHolder(OrderSummaryReportItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
    }
    public void setViewModel(OrderSummaryReportViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }
}