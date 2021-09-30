// Generated by data binding compiler. Do not edit!
package com.zopnote.android.merchant.databinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.zopnote.android.merchant.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class EditDraftInvoiceActBinding extends ViewDataBinding {
  @NonNull
  public final LinearLayout contentView;

  @NonNull
  public final Toolbar toolbar;

  protected EditDraftInvoiceActBinding(Object _bindingComponent, View _root, int _localFieldCount,
      LinearLayout contentView, Toolbar toolbar) {
    super(_bindingComponent, _root, _localFieldCount);
    this.contentView = contentView;
    this.toolbar = toolbar;
  }

  @NonNull
  public static EditDraftInvoiceActBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.edit_draft_invoice_act, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static EditDraftInvoiceActBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<EditDraftInvoiceActBinding>inflateInternal(inflater, R.layout.edit_draft_invoice_act, root, attachToRoot, component);
  }

  @NonNull
  public static EditDraftInvoiceActBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.edit_draft_invoice_act, null, false, component)
   */
  @NonNull
  @Deprecated
  public static EditDraftInvoiceActBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<EditDraftInvoiceActBinding>inflateInternal(inflater, R.layout.edit_draft_invoice_act, null, false, component);
  }

  public static EditDraftInvoiceActBinding bind(@NonNull View view) {
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
  public static EditDraftInvoiceActBinding bind(@NonNull View view, @Nullable Object component) {
    return (EditDraftInvoiceActBinding)bind(component, view, R.layout.edit_draft_invoice_act);
  }
}
