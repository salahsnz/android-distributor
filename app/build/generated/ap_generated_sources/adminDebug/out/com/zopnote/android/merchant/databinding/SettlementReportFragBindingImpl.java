package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class SettlementReportFragBindingImpl extends SettlementReportFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(14);
        sIncludes.setIncludes(0, 
            new String[] {"no_content_error", "network_error", "loading_view"},
            new int[] {4, 5, 6},
            new int[] {com.zopnote.android.merchant.R.layout.no_content_error,
                com.zopnote.android.merchant.R.layout.network_error,
                com.zopnote.android.merchant.R.layout.loading_view});
        sIncludes.setIncludes(1, 
            new String[] {"settlement_amount_breakup_layout", "settlement_details_layout"},
            new int[] {2, 3},
            new int[] {com.zopnote.android.merchant.R.layout.settlement_amount_breakup_layout,
                com.zopnote.android.merchant.R.layout.settlement_details_layout});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.billedAmount, 7);
        sViewsWithIds.put(R.id.currentBilling, 8);
        sViewsWithIds.put(R.id.previousBalance, 9);
        sViewsWithIds.put(R.id.totalAmountUnpaid, 10);
        sViewsWithIds.put(R.id.cashCollectionAmount, 11);
        sViewsWithIds.put(R.id.onlineCollectionAmount, 12);
        sViewsWithIds.put(R.id.reqAdvance, 13);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public SettlementReportFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 14, sIncludes, sViewsWithIds));
    }
    private SettlementReportFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 5
            , (com.zopnote.android.merchant.databinding.SettlementAmountBreakupLayoutBinding) bindings[2]
            , (android.widget.TextView) bindings[7]
            , (android.widget.TextView) bindings[11]
            , (android.widget.LinearLayout) bindings[1]
            , (android.widget.TextView) bindings[8]
            , (com.zopnote.android.merchant.databinding.NoContentErrorBinding) bindings[4]
            , (com.zopnote.android.merchant.databinding.LoadingViewBinding) bindings[6]
            , (com.zopnote.android.merchant.databinding.NetworkErrorBinding) bindings[5]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[9]
            , (android.widget.Button) bindings[13]
            , (com.zopnote.android.merchant.databinding.SettlementDetailsLayoutBinding) bindings[3]
            , (android.widget.TextView) bindings[10]
            );
        setContainedBinding(this.amountBreakupLayout);
        this.contentView.setTag(null);
        setContainedBinding(this.emptyView);
        setContainedBinding(this.loadingView);
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setContainedBinding(this.networkErrorView);
        setContainedBinding(this.settlementDetailsLayout);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x20L;
        }
        amountBreakupLayout.invalidateAll();
        settlementDetailsLayout.invalidateAll();
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
        if (amountBreakupLayout.hasPendingBindings()) {
            return true;
        }
        if (settlementDetailsLayout.hasPendingBindings()) {
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
        amountBreakupLayout.setLifecycleOwner(lifecycleOwner);
        settlementDetailsLayout.setLifecycleOwner(lifecycleOwner);
        emptyView.setLifecycleOwner(lifecycleOwner);
        networkErrorView.setLifecycleOwner(lifecycleOwner);
        loadingView.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeAmountBreakupLayout((com.zopnote.android.merchant.databinding.SettlementAmountBreakupLayoutBinding) object, fieldId);
            case 1 :
                return onChangeLoadingView((com.zopnote.android.merchant.databinding.LoadingViewBinding) object, fieldId);
            case 2 :
                return onChangeEmptyView((com.zopnote.android.merchant.databinding.NoContentErrorBinding) object, fieldId);
            case 3 :
                return onChangeNetworkErrorView((com.zopnote.android.merchant.databinding.NetworkErrorBinding) object, fieldId);
            case 4 :
                return onChangeSettlementDetailsLayout((com.zopnote.android.merchant.databinding.SettlementDetailsLayoutBinding) object, fieldId);
        }
        return false;
    }
    private boolean onChangeAmountBreakupLayout(com.zopnote.android.merchant.databinding.SettlementAmountBreakupLayoutBinding AmountBreakupLayout, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeLoadingView(com.zopnote.android.merchant.databinding.LoadingViewBinding LoadingView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeEmptyView(com.zopnote.android.merchant.databinding.NoContentErrorBinding EmptyView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x4L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeNetworkErrorView(com.zopnote.android.merchant.databinding.NetworkErrorBinding NetworkErrorView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x8L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeSettlementDetailsLayout(com.zopnote.android.merchant.databinding.SettlementDetailsLayoutBinding SettlementDetailsLayout, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x10L;
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
        executeBindingsOn(amountBreakupLayout);
        executeBindingsOn(settlementDetailsLayout);
        executeBindingsOn(emptyView);
        executeBindingsOn(networkErrorView);
        executeBindingsOn(loadingView);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): amountBreakupLayout
        flag 1 (0x2L): loadingView
        flag 2 (0x3L): emptyView
        flag 3 (0x4L): networkErrorView
        flag 4 (0x5L): settlementDetailsLayout
        flag 5 (0x6L): null
    flag mapping end*/
    //end
}