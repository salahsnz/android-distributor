package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class SubscriptionsFragBindingImpl extends SubscriptionsFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(9);
        sIncludes.setIncludes(0, 
            new String[] {"loading_view"},
            new int[] {1},
            new int[] {com.zopnote.android.merchant.R.layout.loading_view});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.contentView, 2);
        sViewsWithIds.put(R.id.gridview, 3);
        sViewsWithIds.put(R.id.recylerView, 4);
        sViewsWithIds.put(R.id.selectedSummaryLayout, 5);
        sViewsWithIds.put(R.id.selectedSummaryHeading, 6);
        sViewsWithIds.put(R.id.selectedSummaryMessage, 7);
        sViewsWithIds.put(R.id.clearAllSelections, 8);
    }
    // views
    @NonNull
    private final android.widget.FrameLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public SubscriptionsFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 9, sIncludes, sViewsWithIds));
    }
    private SubscriptionsFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.TextView) bindings[8]
            , (android.widget.LinearLayout) bindings[2]
            , (android.widget.GridView) bindings[3]
            , (com.zopnote.android.merchant.databinding.LoadingViewBinding) bindings[1]
            , (android.support.v7.widget.RecyclerView) bindings[4]
            , (android.widget.TextView) bindings[6]
            , (android.support.v7.widget.CardView) bindings[5]
            , (android.widget.TextView) bindings[7]
            );
        setContainedBinding(this.loadingView);
        this.mboundView0 = (android.widget.FrameLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
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
        loadingView.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeLoadingView((com.zopnote.android.merchant.databinding.LoadingViewBinding) object, fieldId);
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

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
        executeBindingsOn(loadingView);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): loadingView
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}