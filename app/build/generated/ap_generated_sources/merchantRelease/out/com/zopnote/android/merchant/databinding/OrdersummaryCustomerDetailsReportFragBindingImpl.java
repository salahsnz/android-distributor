package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class OrdersummaryCustomerDetailsReportFragBindingImpl extends OrdersummaryCustomerDetailsReportFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(35);
        sIncludes.setIncludes(0, 
            new String[] {"order_summary_customer_details_layout", "no_content_error", "network_error", "loading_view"},
            new int[] {1, 2, 3, 4},
            new int[] {com.zopnote.android.merchant.R.layout.order_summary_customer_details_layout,
                com.zopnote.android.merchant.R.layout.no_content_error,
                com.zopnote.android.merchant.R.layout.network_error,
                com.zopnote.android.merchant.R.layout.loading_view});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.customerName, 5);
        sViewsWithIds.put(R.id.nameMobileSeparator, 6);
        sViewsWithIds.put(R.id.mobileNumberLayout, 7);
        sViewsWithIds.put(R.id.mobileNumber, 8);
        sViewsWithIds.put(R.id.doorNo, 9);
        sViewsWithIds.put(R.id.addressLine1Layout, 10);
        sViewsWithIds.put(R.id.addressLine1, 11);
        sViewsWithIds.put(R.id.addressLine2, 12);
        sViewsWithIds.put(R.id.email, 13);
        sViewsWithIds.put(R.id.route, 14);
        sViewsWithIds.put(R.id.customerStatus, 15);
        sViewsWithIds.put(R.id.radioGroupPeriod, 16);
        sViewsWithIds.put(R.id.radioBtnToday, 17);
        sViewsWithIds.put(R.id.radioBtnThisWeek, 18);
        sViewsWithIds.put(R.id.radioBtnLastWeek, 19);
        sViewsWithIds.put(R.id.radioBtnThisMonth, 20);
        sViewsWithIds.put(R.id.radioGroupPeriod1, 21);
        sViewsWithIds.put(R.id.radioBtnCustom, 22);
        sViewsWithIds.put(R.id.startDatePickerLayout, 23);
        sViewsWithIds.put(R.id.startDatePicker, 24);
        sViewsWithIds.put(R.id.endDatePickerLayout, 25);
        sViewsWithIds.put(R.id.endDatePicker, 26);
        sViewsWithIds.put(R.id.recyclerView, 27);
        sViewsWithIds.put(R.id.root, 28);
        sViewsWithIds.put(R.id.totalOrders, 29);
        sViewsWithIds.put(R.id.totalBilled, 30);
        sViewsWithIds.put(R.id.totalPaidOrders, 31);
        sViewsWithIds.put(R.id.totalPaid, 32);
        sViewsWithIds.put(R.id.totalUnPaidOrders, 33);
        sViewsWithIds.put(R.id.totalUnPaid, 34);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public OrdersummaryCustomerDetailsReportFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 35, sIncludes, sViewsWithIds));
    }
    private OrdersummaryCustomerDetailsReportFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 4
            , (android.widget.TextView) bindings[11]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.TextView) bindings[12]
            , (android.widget.LinearLayout) bindings[0]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[15]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[13]
            , (com.zopnote.android.merchant.databinding.NoContentErrorBinding) bindings[2]
            , (android.widget.TextView) bindings[26]
            , (android.widget.LinearLayout) bindings[25]
            , (com.zopnote.android.merchant.databinding.LoadingViewBinding) bindings[4]
            , (android.widget.TextView) bindings[8]
            , (android.widget.LinearLayout) bindings[7]
            , (android.widget.TextView) bindings[6]
            , (com.zopnote.android.merchant.databinding.NetworkErrorBinding) bindings[3]
            , (com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsLayoutBinding) bindings[1]
            , (android.widget.RadioButton) bindings[22]
            , (android.widget.RadioButton) bindings[19]
            , (android.widget.RadioButton) bindings[20]
            , (android.widget.RadioButton) bindings[18]
            , (android.widget.RadioButton) bindings[17]
            , (android.widget.RadioGroup) bindings[16]
            , (android.widget.RadioGroup) bindings[21]
            , (android.support.v7.widget.RecyclerView) bindings[27]
            , (android.widget.LinearLayout) bindings[28]
            , (android.widget.TextView) bindings[14]
            , (android.widget.TextView) bindings[24]
            , (android.widget.LinearLayout) bindings[23]
            , (android.widget.TextView) bindings[30]
            , (android.widget.TextView) bindings[29]
            , (android.widget.TextView) bindings[32]
            , (android.widget.TextView) bindings[31]
            , (android.widget.TextView) bindings[34]
            , (android.widget.TextView) bindings[33]
            );
        this.contentView.setTag(null);
        setContainedBinding(this.emptyView);
        setContainedBinding(this.loadingView);
        setContainedBinding(this.networkErrorView);
        setContainedBinding(this.orderSummaryCustomerDetailsLayout);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x10L;
        }
        orderSummaryCustomerDetailsLayout.invalidateAll();
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
        if (orderSummaryCustomerDetailsLayout.hasPendingBindings()) {
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
        orderSummaryCustomerDetailsLayout.setLifecycleOwner(lifecycleOwner);
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
                return onChangeOrderSummaryCustomerDetailsLayout((com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsLayoutBinding) object, fieldId);
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
    private boolean onChangeOrderSummaryCustomerDetailsLayout(com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsLayoutBinding OrderSummaryCustomerDetailsLayout, int fieldId) {
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
        executeBindingsOn(orderSummaryCustomerDetailsLayout);
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
        flag 3 (0x4L): orderSummaryCustomerDetailsLayout
        flag 4 (0x5L): null
    flag mapping end*/
    //end
}