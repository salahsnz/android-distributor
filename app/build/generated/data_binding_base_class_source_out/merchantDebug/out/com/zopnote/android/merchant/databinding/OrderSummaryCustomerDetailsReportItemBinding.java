// Generated by data binding compiler. Do not edit!
package com.zopnote.android.merchant.databinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zopnote.android.merchant.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class OrderSummaryCustomerDetailsReportItemBinding extends ViewDataBinding {
  @NonNull
  public final TextView invoiceAmount;

  @NonNull
  public final TextView invoiceDate;

  @NonNull
  public final TextView invoiceNumber;

  @NonNull
  public final TextView status;

  protected OrderSummaryCustomerDetailsReportItemBinding(Object _bindingComponent, View _root,
      int _localFieldCount, TextView invoiceAmount, TextView invoiceDate, TextView invoiceNumber,
      TextView status) {
    super(_bindingComponent, _root, _localFieldCount);
    this.invoiceAmount = invoiceAmount;
    this.invoiceDate = invoiceDate;
    this.invoiceNumber = invoiceNumber;
    this.status = status;
  }

  @NonNull
  public static OrderSummaryCustomerDetailsReportItemBinding inflate(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.order_summary_customer_details_report_item, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static OrderSummaryCustomerDetailsReportItemBinding inflate(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup root, boolean attachToRoot,
      @Nullable Object component) {
    return ViewDataBinding.<OrderSummaryCustomerDetailsReportItemBinding>inflateInternal(inflater, R.layout.order_summary_customer_details_report_item, root, attachToRoot, component);
  }

  @NonNull
  public static OrderSummaryCustomerDetailsReportItemBinding inflate(
      @NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.order_summary_customer_details_report_item, null, false, component)
   */
  @NonNull
  @Deprecated
  public static OrderSummaryCustomerDetailsReportItemBinding inflate(
      @NonNull LayoutInflater inflater, @Nullable Object component) {
    return ViewDataBinding.<OrderSummaryCustomerDetailsReportItemBinding>inflateInternal(inflater, R.layout.order_summary_customer_details_report_item, null, false, component);
  }

  public static OrderSummaryCustomerDetailsReportItemBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static OrderSummaryCustomerDetailsReportItemBinding bind(@NonNull View view,
      @Nullable Object component) {
    return (OrderSummaryCustomerDetailsReportItemBinding)bind(component, view, R.layout.order_summary_customer_details_report_item);
  }
}
