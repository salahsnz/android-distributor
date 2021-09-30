package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class InvoiceFragBindingImpl extends InvoiceFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(36);
        sIncludes.setIncludes(1, 
            new String[] {"network_error", "loading_view", "no_content_available"},
            new int[] {2, 3, 4},
            new int[] {com.zopnote.android.merchant.R.layout.network_error,
                com.zopnote.android.merchant.R.layout.loading_view,
                com.zopnote.android.merchant.R.layout.no_content_available});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.merchantName, 5);
        sViewsWithIds.put(R.id.totalDueAtTop, 6);
        sViewsWithIds.put(R.id.name, 7);
        sViewsWithIds.put(R.id.address, 8);
        sViewsWithIds.put(R.id.invoiceNumber, 9);
        sViewsWithIds.put(R.id.invoiceDate, 10);
        sViewsWithIds.put(R.id.billingPeriodLayout, 11);
        sViewsWithIds.put(R.id.billingPeriod, 12);
        sViewsWithIds.put(R.id.dueDateLayout, 13);
        sViewsWithIds.put(R.id.dueDate, 14);
        sViewsWithIds.put(R.id.radioGroupPeriod, 15);
        sViewsWithIds.put(R.id.radioBtnThisMonth, 16);
        sViewsWithIds.put(R.id.radioBtnThisWeek, 17);
        sViewsWithIds.put(R.id.radioGroupPeriod1, 18);
        sViewsWithIds.put(R.id.radioBtnCustom, 19);
        sViewsWithIds.put(R.id.startDatePickerLayout, 20);
        sViewsWithIds.put(R.id.startDatePicker, 21);
        sViewsWithIds.put(R.id.endDatePickerLayout, 22);
        sViewsWithIds.put(R.id.endDatePicker, 23);
        sViewsWithIds.put(R.id.invoiceItemsLayout, 24);
        sViewsWithIds.put(R.id.contentView, 25);
        sViewsWithIds.put(R.id.linearHeaderContent, 26);
        sViewsWithIds.put(R.id.recyclerView, 27);
        sViewsWithIds.put(R.id.linearHeaderContentTotal, 28);
        sViewsWithIds.put(R.id.linearPreviousMonth, 29);
        sViewsWithIds.put(R.id.previousMonthBalance, 30);
        sViewsWithIds.put(R.id.linearFilteredAdvanceOrPrevious, 31);
        sViewsWithIds.put(R.id.filteredAdvanceOrPrevious, 32);
        sViewsWithIds.put(R.id.previousBalanceAtBottom, 33);
        sViewsWithIds.put(R.id.totalDueAtBottom, 34);
        sViewsWithIds.put(R.id.updateInvoiceItemButton, 35);
    }
    // views
    @NonNull
    private final android.widget.ScrollView mboundView0;
    @NonNull
    private final android.widget.FrameLayout mboundView1;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public InvoiceFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 36, sIncludes, sViewsWithIds));
    }
    private InvoiceFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 3
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[12]
            , (android.widget.LinearLayout) bindings[11]
            , (android.widget.LinearLayout) bindings[25]
            , (android.widget.TextView) bindings[14]
            , (android.widget.LinearLayout) bindings[13]
            , (android.widget.TextView) bindings[23]
            , (android.widget.LinearLayout) bindings[22]
            , (android.widget.TextView) bindings[32]
            , (android.widget.TextView) bindings[10]
            , (android.widget.LinearLayout) bindings[24]
            , (android.widget.TextView) bindings[9]
            , (android.widget.LinearLayout) bindings[31]
            , (android.widget.LinearLayout) bindings[26]
            , (android.widget.LinearLayout) bindings[28]
            , (android.widget.LinearLayout) bindings[29]
            , (com.zopnote.android.merchant.databinding.LoadingViewBinding) bindings[3]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[7]
            , (com.zopnote.android.merchant.databinding.NetworkErrorBinding) bindings[2]
            , (com.zopnote.android.merchant.databinding.NoContentAvailableBinding) bindings[4]
            , (android.widget.TextView) bindings[33]
            , (android.widget.TextView) bindings[30]
            , (android.widget.RadioButton) bindings[19]
            , (android.widget.RadioButton) bindings[16]
            , (android.widget.RadioButton) bindings[17]
            , (android.widget.RadioGroup) bindings[15]
            , (android.widget.RadioGroup) bindings[18]
            , (android.support.v7.widget.RecyclerView) bindings[27]
            , (android.widget.TextView) bindings[21]
            , (android.widget.LinearLayout) bindings[20]
            , (android.widget.TextView) bindings[34]
            , (android.widget.TextView) bindings[6]
            , (android.widget.TextView) bindings[35]
            );
        setContainedBinding(this.loadingView);
        this.mboundView0 = (android.widget.ScrollView) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.FrameLayout) bindings[1];
        this.mboundView1.setTag(null);
        setContainedBinding(this.networkErrorView);
        setContainedBinding(this.noDataView);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x8L;
        }
        networkErrorView.invalidateAll();
        loadingView.invalidateAll();
        noDataView.invalidateAll();
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        if (networkErrorView.hasPendingBindings()) {
            return true;
        }
        if (loadingView.hasPendingBindings()) {
            return true;
        }
        if (noDataView.hasPendingBindings()) {
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
        networkErrorView.setLifecycleOwner(lifecycleOwner);
        loadingView.setLifecycleOwner(lifecycleOwner);
        noDataView.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeNoDataView((com.zopnote.android.merchant.databinding.NoContentAvailableBinding) object, fieldId);
            case 1 :
                return onChangeNetworkErrorView((com.zopnote.android.merchant.databinding.NetworkErrorBinding) object, fieldId);
            case 2 :
                return onChangeLoadingView((com.zopnote.android.merchant.databinding.LoadingViewBinding) object, fieldId);
        }
        return false;
    }
    private boolean onChangeNoDataView(com.zopnote.android.merchant.databinding.NoContentAvailableBinding NoDataView, int fieldId) {
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

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
        executeBindingsOn(networkErrorView);
        executeBindingsOn(loadingView);
        executeBindingsOn(noDataView);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): noDataView
        flag 1 (0x2L): networkErrorView
        flag 2 (0x3L): loadingView
        flag 3 (0x4L): null
    flag mapping end*/
    //end
}