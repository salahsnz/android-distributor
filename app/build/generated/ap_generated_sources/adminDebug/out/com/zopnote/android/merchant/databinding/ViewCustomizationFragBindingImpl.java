package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ViewCustomizationFragBindingImpl extends ViewCustomizationFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(6);
        sIncludes.setIncludes(1, 
            new String[] {"view_prepaid_customization_layout", "view_postpaid_customization_layout"},
            new int[] {2, 3},
            new int[] {com.zopnote.android.merchant.R.layout.view_prepaid_customization_layout,
                com.zopnote.android.merchant.R.layout.view_postpaid_customization_layout});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.name, 4);
        sViewsWithIds.put(R.id.editButton, 5);
    }
    // views
    @NonNull
    private final android.widget.ScrollView mboundView0;
    @NonNull
    private final android.widget.LinearLayout mboundView1;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ViewCustomizationFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 6, sIncludes, sViewsWithIds));
    }
    private ViewCustomizationFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 2
            , (android.widget.Button) bindings[5]
            , (android.widget.TextView) bindings[4]
            , (com.zopnote.android.merchant.databinding.ViewPostpaidCustomizationLayoutBinding) bindings[3]
            , (com.zopnote.android.merchant.databinding.ViewPrepaidCustomizationLayoutBinding) bindings[2]
            );
        this.mboundView0 = (android.widget.ScrollView) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.LinearLayout) bindings[1];
        this.mboundView1.setTag(null);
        setContainedBinding(this.viewPostpaidInfoLayout);
        setContainedBinding(this.viewPrepaidInfoLayout);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
        }
        viewPrepaidInfoLayout.invalidateAll();
        viewPostpaidInfoLayout.invalidateAll();
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        if (viewPrepaidInfoLayout.hasPendingBindings()) {
            return true;
        }
        if (viewPostpaidInfoLayout.hasPendingBindings()) {
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
        viewPrepaidInfoLayout.setLifecycleOwner(lifecycleOwner);
        viewPostpaidInfoLayout.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeViewPostpaidInfoLayout((com.zopnote.android.merchant.databinding.ViewPostpaidCustomizationLayoutBinding) object, fieldId);
            case 1 :
                return onChangeViewPrepaidInfoLayout((com.zopnote.android.merchant.databinding.ViewPrepaidCustomizationLayoutBinding) object, fieldId);
        }
        return false;
    }
    private boolean onChangeViewPostpaidInfoLayout(com.zopnote.android.merchant.databinding.ViewPostpaidCustomizationLayoutBinding ViewPostpaidInfoLayout, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeViewPrepaidInfoLayout(com.zopnote.android.merchant.databinding.ViewPrepaidCustomizationLayoutBinding ViewPrepaidInfoLayout, int fieldId) {
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
        executeBindingsOn(viewPrepaidInfoLayout);
        executeBindingsOn(viewPostpaidInfoLayout);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): viewPostpaidInfoLayout
        flag 1 (0x2L): viewPrepaidInfoLayout
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}