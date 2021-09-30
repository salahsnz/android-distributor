package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class EditCustomerContentBindingImpl extends EditCustomerContentBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.mobileNumber, 1);
        sViewsWithIds.put(R.id.pickContact, 2);
        sViewsWithIds.put(R.id.email, 3);
        sViewsWithIds.put(R.id.name, 4);
        sViewsWithIds.put(R.id.doorNumber, 5);
        sViewsWithIds.put(R.id.addressLine1SpinnerContainer, 6);
        sViewsWithIds.put(R.id.addressLine2, 7);
        sViewsWithIds.put(R.id.route, 8);
        sViewsWithIds.put(R.id.cancelButton, 9);
        sViewsWithIds.put(R.id.submitButton, 10);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public EditCustomerContentBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 11, sIncludes, sViewsWithIds));
    }
    private EditCustomerContentBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.LinearLayout) bindings[6]
            , (android.widget.Spinner) bindings[7]
            , (android.widget.Button) bindings[9]
            , (android.support.design.widget.TextInputEditText) bindings[5]
            , (android.support.design.widget.TextInputEditText) bindings[3]
            , (com.zopnote.android.merchant.util.MobileNumberEditText) bindings[1]
            , (android.support.design.widget.TextInputEditText) bindings[4]
            , (android.widget.ImageView) bindings[2]
            , (android.widget.LinearLayout) bindings[0]
            , (android.widget.Spinner) bindings[8]
            , (android.widget.Button) bindings[10]
            );
        this.root.setTag(null);
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