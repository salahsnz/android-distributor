package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ProductSetupFragBindingImpl extends ProductSetupFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(17);
        sIncludes.setIncludes(0, 
            new String[] {"network_error"},
            new int[] {3},
            new int[] {com.zopnote.android.merchant.R.layout.network_error});
        sIncludes.setIncludes(1, 
            new String[] {"loading_view"},
            new int[] {2},
            new int[] {com.zopnote.android.merchant.R.layout.loading_view});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.merchantName, 4);
        sViewsWithIds.put(R.id.merchantBusinessName, 5);
        sViewsWithIds.put(R.id.productType, 6);
        sViewsWithIds.put(R.id.profilePic, 7);
        sViewsWithIds.put(R.id.productName, 8);
        sViewsWithIds.put(R.id.viewBillRelative, 9);
        sViewsWithIds.put(R.id.iv_bill, 10);
        sViewsWithIds.put(R.id.deleteImg, 11);
        sViewsWithIds.put(R.id.takePhoto, 12);
        sViewsWithIds.put(R.id.pickPhoto, 13);
        sViewsWithIds.put(R.id.productPrice, 14);
        sViewsWithIds.put(R.id.addProductSetup, 15);
        sViewsWithIds.put(R.id.closeProductSetup, 16);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    @NonNull
    private final android.widget.LinearLayout mboundView1;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ProductSetupFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 17, sIncludes, sViewsWithIds));
    }
    private ProductSetupFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 2
            , (android.widget.Button) bindings[15]
            , (android.widget.Button) bindings[16]
            , (android.widget.ImageView) bindings[11]
            , (android.widget.ImageView) bindings[10]
            , (com.zopnote.android.merchant.databinding.LoadingViewBinding) bindings[2]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[4]
            , (com.zopnote.android.merchant.databinding.NetworkErrorBinding) bindings[3]
            , (android.widget.LinearLayout) bindings[13]
            , (android.support.design.widget.TextInputEditText) bindings[8]
            , (android.support.design.widget.TextInputEditText) bindings[14]
            , (android.widget.TextView) bindings[6]
            , (android.widget.ImageView) bindings[7]
            , (android.widget.LinearLayout) bindings[12]
            , (android.widget.RelativeLayout) bindings[9]
            );
        setContainedBinding(this.loadingView);
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
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
                mDirtyFlags = 0x4L;
        }
        loadingView.invalidateAll();
        networkErrorView.invalidateAll();
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
        executeBindingsOn(loadingView);
        executeBindingsOn(networkErrorView);
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