package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ShopSetupFragBindingImpl extends ShopSetupFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(24);
        sIncludes.setIncludes(0, 
            new String[] {"network_error", "loading_view"},
            new int[] {1, 2},
            new int[] {com.zopnote.android.merchant.R.layout.network_error,
                com.zopnote.android.merchant.R.layout.loading_view});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.merchantName, 3);
        sViewsWithIds.put(R.id.merchantBusinessName, 4);
        sViewsWithIds.put(R.id.profilePic, 5);
        sViewsWithIds.put(R.id.radioGroupServices, 6);
        sViewsWithIds.put(R.id.radioButtonNewsPaper, 7);
        sViewsWithIds.put(R.id.img_Newspaper, 8);
        sViewsWithIds.put(R.id.radioButtonGrocery, 9);
        sViewsWithIds.put(R.id.img_grocery, 10);
        sViewsWithIds.put(R.id.radioButtonDhobi, 11);
        sViewsWithIds.put(R.id.img_laundry, 12);
        sViewsWithIds.put(R.id.radioButtonDistri, 13);
        sViewsWithIds.put(R.id.img_Distributor, 14);
        sViewsWithIds.put(R.id.radioGroupPricing, 15);
        sViewsWithIds.put(R.id.radioButtonDaily, 16);
        sViewsWithIds.put(R.id.radioButtonMonthly, 17);
        sViewsWithIds.put(R.id.radioGroupPaidType, 18);
        sViewsWithIds.put(R.id.radioButtonPrePaid, 19);
        sViewsWithIds.put(R.id.radioButtonPostPaid, 20);
        sViewsWithIds.put(R.id.submitShopSetup, 21);
        sViewsWithIds.put(R.id.skipShopSetup, 22);
        sViewsWithIds.put(R.id.closeShopSetup, 23);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ShopSetupFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 24, sIncludes, sViewsWithIds));
    }
    private ShopSetupFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 2
            , (android.widget.Button) bindings[23]
            , (android.widget.ImageView) bindings[14]
            , (android.widget.ImageView) bindings[10]
            , (android.widget.ImageView) bindings[12]
            , (android.widget.ImageView) bindings[8]
            , (com.zopnote.android.merchant.databinding.LoadingViewBinding) bindings[2]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[3]
            , (com.zopnote.android.merchant.databinding.NetworkErrorBinding) bindings[1]
            , (android.widget.ImageView) bindings[5]
            , (android.widget.RadioButton) bindings[16]
            , (android.widget.RadioButton) bindings[11]
            , (android.widget.RadioButton) bindings[13]
            , (android.widget.RadioButton) bindings[9]
            , (android.widget.RadioButton) bindings[17]
            , (android.widget.RadioButton) bindings[7]
            , (android.widget.RadioButton) bindings[20]
            , (android.widget.RadioButton) bindings[19]
            , (android.widget.RadioGroup) bindings[18]
            , (android.widget.RadioGroup) bindings[15]
            , (android.widget.RadioGroup) bindings[6]
            , (android.widget.Button) bindings[22]
            , (android.widget.Button) bindings[21]
            );
        setContainedBinding(this.loadingView);
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
        this.mboundView0.setTag(null);
        setContainedBinding(this.networkErrorView);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
        }
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
        networkErrorView.setLifecycleOwner(lifecycleOwner);
        loadingView.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeLoadingView((com.zopnote.android.merchant.databinding.LoadingViewBinding) object, fieldId);
            case 1 :
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
    private boolean onChangeNetworkErrorView(com.zopnote.android.merchant.databinding.NetworkErrorBinding NetworkErrorView, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
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
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): loadingView
        flag 1 (0x2L): networkErrorView
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}