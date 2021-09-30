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

public abstract class CustomerSubscriptionItemBinding extends ViewDataBinding {
  @NonNull
  public final LinearLayout addPause;

  @NonNull
  public final TextView customizedTag;

  @NonNull
  public final TextView name;

  @NonNull
  public final TextView stopDuration;

  @NonNull
  public final TextView subscriptionDuration;

  @NonNull
  public final TextView tag;

  protected CustomerSubscriptionItemBinding(Object _bindingComponent, View _root,
      int _localFieldCount, LinearLayout addPause, TextView customizedTag, TextView name,
      TextView stopDuration, TextView subscriptionDuration, TextView tag) {
    super(_bindingComponent, _root, _localFieldCount);
    this.addPause = addPause;
    this.customizedTag = customizedTag;
    this.name = name;
    this.stopDuration = stopDuration;
    this.subscriptionDuration = subscriptionDuration;
    this.tag = tag;
  }

  @NonNull
  public static CustomerSubscriptionItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.customer_subscription_item, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static CustomerSubscriptionItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<CustomerSubscriptionItemBinding>inflateInternal(inflater, R.layout.customer_subscription_item, root, attachToRoot, component);
  }

  @NonNull
  public static CustomerSubscriptionItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.customer_subscription_item, null, false, component)
   */
  @NonNull
  @Deprecated
  public static CustomerSubscriptionItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<CustomerSubscriptionItemBinding>inflateInternal(inflater, R.layout.customer_subscription_item, null, false, component);
  }

  public static CustomerSubscriptionItemBinding bind(@NonNull View view) {
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
  public static CustomerSubscriptionItemBinding bind(@NonNull View view,
      @Nullable Object component) {
    return (CustomerSubscriptionItemBinding)bind(component, view, R.layout.customer_subscription_item);
  }
}
