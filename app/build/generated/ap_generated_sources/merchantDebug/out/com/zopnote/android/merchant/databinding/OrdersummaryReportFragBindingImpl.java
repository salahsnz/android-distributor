package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class OrdersummaryReportFragBindingImpl extends OrdersummaryReportFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(25);
        sIncludes.setIncludes(0, 
            new String[] {"network_error", "loading_view"},
            new int[] {4, 5},
            new int[] {com.zopnote.android.merchant.R.layout.network_error,
                com.zopnote.android.merchant.R.layout.loading_view});
        sIncludes.setIncludes(1, 
            new String[] {"order_summary_details_layout", "no_content_error"},
            new int[] {2, 3},
            new int[] {com.zopnote.android.merchant.R.layout.order_summary_details_layout,
                com.zopnote.android.merchant.R.layout.no_content_error});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.radioGroupPeriod, 6);
        sViewsWithIds.put(R.id.radioBtnToday, 7);
        sViewsWithIds.put(R.id.radioBtnThisWeek, 8);
        sViewsWithIds.put(R.id.radioBtnLastWeek, 9);
        sViewsWithIds.put(R.id.radioBtnThisMonth, 10);
        sViewsWithIds.put(R.id.radioGroupPeriod1, 11);
        sViewsWithIds.put(R.id.radioBtnCustom, 12);
        sViewsWithIds.put(R.id.startDatePickerLayout, 13);
        sViewsWithIds.put(R.id.startDatePicker, 14);
        sViewsWithIds.put(R.id.endDatePickerLayout, 15);
        sViewsWithIds.put(R.id.endDatePicker, 16);
        sViewsWithIds.put(R.id.recyclerView, 17);
        sViewsWithIds.put(R.id.root, 18);
        sViewsWithIds.put(R.id.totalOrders, 19);
        sViewsWithIds.put(R.id.totalBilled, 20);
        sViewsWithIds.put(R.id.totalPaidOrders, 21);
        sViewsWithIds.put(R.id.totalPaid, 22);
        sViewsWithIds.put(R.id.totalUnPaidOrders, 23);
        sViewsWithIds.put(R.id.totalUnPaid, 24);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public OrdersummaryReportFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 25, sIncludes, sViewsWithIds));
    }
    private OrdersummaryReportFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 4
            , (android.widget.LinearLayout) bindings[1]
            , (com.zopnote.android.merchant.databinding.NoContentErrorBinding) bindings[3]
            , (android.widget.TextView) bindings[16]
            , (android.widget.LinearLayout) bindings[15]
            , (com.zopnote.android.merchant.databinding.LoadingViewBinding) bindings[5]
            , (com.zopnote.android.merchant.databinding.NetworkErrorBinding) bindings[4]
            , (com.zopnote.android.merchant.databinding.OrderSummaryDetailsLayoutBinding) bindings[2]
            , (android.widget.RadioButton) bindings[12]
            , (android.widget.RadioButton) bindings[9]
            , (android.widget.RadioButton) bindings[10]
            , (android.widget.RadioButton) bindings[8]
            , (android.widget.RadioButton) bindings[7]
            , (android.widget.RadioGroup) bindings[6]
            , (android.widget.RadioGroup) bindings[11]
            , (android.support.v7.widget.RecyclerView) bindings[17]
            , (android.widget.LinearLayout) bindings[18]
            , (android.widget.TextView) bindings[14]
            , (android.widget.LinearLayout) bindings[13]
            , (android.widget.TextView) bindings[20]
            , (android.widget.TextView) bindings[19]
            , (android.widget.TextView) bindings[22]
            , (android.widget.TextView) bindings[21]
            , (android.widget.TextView) bindings[24]
            , (android.widget.TextView) bindings[23]
            );
        this.contentView.setTag(null);
        setContainedBinding(this.emptyView);
        setContainedBinding(this.loadingView);
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setContainedBinding(this.networkErrorView);
        setContainedBinding(this.orderSummaryDetailsLayout);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x10L;
        }
        orderSummaryDetailsLayout.invalidateAll();
        emptyView.invalidateAll();
        networkErrorView.invalidateAll();
        loadingView.invalidateAll();
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        if (orderSummaryDetailsLayout.hasPendingBindings()) {
            return true;
        }
        if (emptyView.hasPendingBindings()) {
            return true;
        }
        if (networkErrorView.hasPendingBindings()) {
            return true;
        }
        if (loadingView.hasPendingBindings()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    public void setLifecycleOwner(@Nullable android.arch.lifecycle.LifecycleOwner lifecycleOwner) {
        super.setLifecycleOwner(lifecycleOwner);
        orderSummaryDetailsLayout.setLifecycleOwner(lifecycleOwner);
        emptyView.setLifecycleOwner(lifecycleOwner);
        networkErrorView.setLifecycleOwner(lifecycleOwner);
        loadingView.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeEmptyView((com.zopnote.android.merchant.databinding.NoContentErrorBinding) object, fieldId);
            case 1 :
                return onChangeNetworkErrorView((com.zopnote.android.merchant.databinding.NetworkErrorBinding) object, fieldId);
            case 2 :
                return onChangeLoadingView((com.zopnote.android.merchant.databinding.LoadingViewBinding) object, fieldId);
            case 3 :
                return onChangeOrderSummaryDetailsLayout((com.zopnote.android.merchant.databinding.OrderSummaryDetailsLayoutBinding) object, fieldId);
        }
        return false;
    }
    private boolean onChangeEmptyView(com.zopnote.android.merchant.databinding.NoContentErrorBinding EmptyView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeNetworkErrorView(com.zopnote.android.merchant.databinding.NetworkErrorBinding NetworkErrorView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeLoadingView(com.zopnote.android.merchant.databinding.LoadingViewBinding LoadingView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x4L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeOrderSummaryDetailsLayout(com.zopnote.android.merchant.databinding.OrderSummaryDetailsLayoutBinding OrderSummaryDetailsLayout, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x8L;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
        executeBindingsOn(orderSummaryDetailsLayout);
        executeBindingsOn(emptyView);
        executeBindingsOn(networkErrorView);
        executeBindingsOn(loadingView);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): emptyView
        flag 1 (0x2L): networkErrorView
        flag 2 (0x3L): loadingView
        flag 3 (0x4L): orderSummaryDetailsLayout
        flag 4 (0x5L): null
    flag mapping end*/
    //end
}