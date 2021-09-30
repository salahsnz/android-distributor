package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ManageSubscriptionItemBindingImpl extends ManageSubscriptionItemBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.name, 1);
        sViewsWithIds.put(R.id.customizedTag, 2);
        sViewsWithIds.put(R.id.startDateLayout, 3);
        sViewsWithIds.put(R.id.startDate, 4);
        sViewsWithIds.put(R.id.endDateLayout, 5);
        sViewsWithIds.put(R.id.endDate, 6);
        sViewsWithIds.put(R.id.tagLayout, 7);
        sViewsWithIds.put(R.id.tag, 8);
        sViewsWithIds.put(R.id.pausesLayout, 9);
        sViewsWithIds.put(R.id.pausesContainer, 10);
        sViewsWithIds.put(R.id.subscriptionActionsButton, 11);
    }
    // views
    @NonNull
    private final android.widget.FrameLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ManageSubscriptionItemBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 12, sIncludes, sViewsWithIds));
    }
    private ManageSubscriptionItemBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.TextView) bindings[2]
            , (android.widget.TextView) bindings[6]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.TextView) bindings[1]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.TextView) bindings[4]
            , (android.widget.LinearLayout) bindings[3]
            , (android.widget.ImageView) bindings[11]
            , (android.widget.TextView) bindings[8]
            , (android.widget.LinearLayout) bindings[7]
            );
        this.mboundView0 = (android.widget.FrameLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
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
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}