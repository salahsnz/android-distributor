package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class DraftInvoiceReportItemBindingImpl extends DraftInvoiceReportItemBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.doorNumber, 1);
        sViewsWithIds.put(R.id.addressLine1Layout, 2);
        sViewsWithIds.put(R.id.addressLine1, 3);
        sViewsWithIds.put(R.id.addressLine2, 4);
        sViewsWithIds.put(R.id.invoiceItemsContainer, 5);
        sViewsWithIds.put(R.id.totalDueLayout, 6);
        sViewsWithIds.put(R.id.totalDueAtBottom, 7);
        sViewsWithIds.put(R.id.notesLayout, 8);
        sViewsWithIds.put(R.id.notes, 9);
        sViewsWithIds.put(R.id.highlightBar, 10);
    }
    // views
    @NonNull
    private final android.widget.FrameLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public DraftInvoiceReportItemBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 11, sIncludes, sViewsWithIds));
    }
    private DraftInvoiceReportItemBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.TextView) bindings[3]
            , (android.widget.LinearLayout) bindings[2]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[1]
            , (android.view.View) bindings[10]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.TextView) bindings[9]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.TextView) bindings[7]
            , (android.widget.LinearLayout) bindings[6]
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