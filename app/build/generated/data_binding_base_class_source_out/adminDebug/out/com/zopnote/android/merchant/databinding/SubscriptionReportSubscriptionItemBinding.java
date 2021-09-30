// Generated by data binding compiler. Do not edit!
package com.zopnote.android.merchant.databinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zopnote.android.merchant.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class SubscriptionReportSubscriptionItemBinding extends ViewDataBinding {
  @NonNull
  public final TextView name;

  @NonNull
  public final LinearLayout pausesContainer;

  @NonNull
  public final View subscriptionDivider;

  @NonNull
  public final TextView subscriptionEndDate;

  @NonNull
  public final TextView subscriptionStartDate;

  protected SubscriptionReportSubscriptionItemBinding(Object _bindingComponent, View _root,
      int _localFieldCount, TextView name, LinearLayout pausesContainer, View subscriptionDivider,
      TextView subscriptionEndDate, TextView subscriptionStartDate) {
    super(_bindingComponent, _root, _localFieldCount);
    this.name = name;
    this.pausesContainer = pausesContainer;
    this.subscriptionDivider = subscriptionDivider;
    this.subscriptionEndDate = subscriptionEndDate;
    this.subscriptionStartDate = subscriptionStartDate;
  }

  @NonNull
  public static SubscriptionReportSubscriptionItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.subscription_report_subscription_item, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static SubscriptionReportSubscriptionItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<SubscriptionReportSubscriptionItemBinding>inflateInternal(inflater, R.layout.subscription_report_subscription_item, root, attachToRoot, component);
  }

  @NonNull
  public static SubscriptionReportSubscriptionItemBinding inflate(
      @NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.subscription_report_subscription_item, null, false, component)
   */
  @NonNull
  @Deprecated
  public static SubscriptionReportSubscriptionItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<SubscriptionReportSubscriptionItemBinding>inflateInternal(inflater, R.layout.subscription_report_subscription_item, null, false, component);
  }

  public static SubscriptionReportSubscriptionItemBinding bind(@NonNull View view) {
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
  public static SubscriptionReportSubscriptionItemBinding bind(@NonNull View view,
      @Nullable Object component) {
    return (SubscriptionReportSubscriptionItemBinding)bind(component, view, R.layout.subscription_report_subscription_item);
  }
}
