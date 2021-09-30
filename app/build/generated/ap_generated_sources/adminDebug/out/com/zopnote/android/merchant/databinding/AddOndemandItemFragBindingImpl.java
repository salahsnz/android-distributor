package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class AddOndemandItemFragBindingImpl extends AddOndemandItemFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(20);
        sIncludes.setIncludes(0, 
            new String[] {"no_content_available"},
            new int[] {4},
            new int[] {com.zopnote.android.merchant.R.layout.no_content_available});
        sIncludes.setIncludes(1, 
            new String[] {"loading_view", "network_error"},
            new int[] {2, 3},
            new int[] {com.zopnote.android.merchant.R.layout.loading_view,
                com.zopnote.android.merchant.R.layout.network_error});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.contentView, 5);
        sViewsWithIds.put(R.id.name, 6);
        sViewsWithIds.put(R.id.paperPricingModeSelectionLayout, 7);
        sViewsWithIds.put(R.id.productItemContainer, 8);
        sViewsWithIds.put(R.id.convenienceChargeLayout, 9);
        sViewsWithIds.put(R.id.convenienceCharge, 10);
        sViewsWithIds.put(R.id.magazinePricingModeSelectionLayout, 11);
        sViewsWithIds.put(R.id.totalItemPrice, 12);
        sViewsWithIds.put(R.id.captureBill, 13);
        sViewsWithIds.put(R.id.viewBillRelative, 14);
        sViewsWithIds.put(R.id.iv_bill, 15);
        sViewsWithIds.put(R.id.deleteImg, 16);
        sViewsWithIds.put(R.id.saveWithPaymentReq, 17);
        sViewsWithIds.put(R.id.submitOnDemandItem, 18);
        sViewsWithIds.put(R.id.submitWithPaymentLink, 19);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    @NonNull
    private final android.widget.LinearLayout mboundView1;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public AddOndemandItemFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 20, sIncludes, sViewsWithIds));
    }
    private AddOndemandItemFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 3
            , (android.widget.LinearLayout) bindings[13]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.EditText) bindings[10]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.ImageView) bindings[16]
            , (com.zopnote.android.merchant.databinding.NoContentAvailableBinding) bindings[4]
            , (android.widget.ImageView) bindings[15]
            , (com.zopnote.android.merchant.databinding.LoadingViewBinding) bindings[2]
            , (android.widget.LinearLayout) bindings[11]
            , (android.widget.TextView) bindings[6]
            , (com.zopnote.android.merchant.databinding.NetworkErrorBinding) bindings[3]
            , (android.widget.LinearLayout) bindings[7]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.CheckBox) bindings[17]
            , (android.widget.Button) bindings[18]
            , (android.widget.Button) bindings[19]
            , (android.widget.TextView) bindings[12]
            , (android.widget.RelativeLayout) bindings[14]
            );
        setContainedBinding(this.emptyView);
        setContainedBinding(this.loadingView);
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.LinearLayout) bindings[1];
        this.mboundView1.setTag(null);
        setContainedBinding(this.networkErrorView);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x8L;
        }
        loadingView.invalidateAll();
        networkErrorView.invalidateAll();
        emptyView.invalidateAll();
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        if (loadingView.hasPendingBindings()) {
            return true;
        }
        if (networkErrorView.hasPendingBindings()) {
            return true;
        }
        if (emptyView.hasPendingBindings()) {
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
        loadingView.setLifecycleOwner(lifecycleOwner);
        networkErrorView.setLifecycleOwner(lifecycleOwner);
        emptyView.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeLoadingView((com.zopnote.android.merchant.databinding.LoadingViewBinding) object, fieldId);
            case 1 :
                return onChangeEmptyView((com.zopnote.android.merchant.databinding.NoContentAvailableBinding) object, fieldId);
            case 2 :
                return onChangeNetworkErrorView((com.zopnote.android.merchant.databinding.NetworkErrorBinding) object, fieldId);
        }
        return false;
    }
    private boolean onChangeLoadingView(com.zopnote.android.merchant.databinding.LoadingViewBinding LoadingView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeEmptyView(com.zopnote.android.merchant.databinding.NoContentAvailableBinding EmptyView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeNetworkErrorView(com.zopnote.android.merchant.databinding.NetworkErrorBinding NetworkErrorView, int fieldId) {
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
        executeBindingsOn(loadingView);
        executeBindingsOn(networkErrorView);
        executeBindingsOn(emptyView);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): loadingView
        flag 1 (0x2L): emptyView
        flag 2 (0x3L): networkErrorView
        flag 3 (0x4L): null
    flag mapping end*/
    //end
}