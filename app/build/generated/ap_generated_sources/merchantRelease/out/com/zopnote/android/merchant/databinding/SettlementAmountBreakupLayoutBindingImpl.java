package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class SettlementAmountBreakupLayoutBindingImpl extends SettlementAmountBreakupLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.defaultTL, 1);
        sViewsWithIds.put(R.id.defaultL, 2);
        sViewsWithIds.put(R.id.TotInvPro, 3);
        sViewsWithIds.put(R.id.totalNoOfInvoicesProcessed, 4);
        sViewsWithIds.put(R.id.TotOrdPro, 5);
        sViewsWithIds.put(R.id.totalNoOfOrdersProcessed, 6);
        sViewsWithIds.put(R.id.commission, 7);
        sViewsWithIds.put(R.id.settledAmount, 8);
        sViewsWithIds.put(R.id.zopnote_charges, 9);
        sViewsWithIds.put(R.id.lessCharges, 10);
        sViewsWithIds.put(R.id.gstCharges, 11);
        sViewsWithIds.put(R.id.divider2, 12);
        sViewsWithIds.put(R.id.subscription, 13);
        sViewsWithIds.put(R.id.settledAmountOD, 14);
        sViewsWithIds.put(R.id.zopnote_subscription_charges, 15);
        sViewsWithIds.put(R.id.sub_lessCharges, 16);
        sViewsWithIds.put(R.id.bankCharges, 17);
        sViewsWithIds.put(R.id.gst, 18);
        sViewsWithIds.put(R.id.transferredAmount, 19);
        sViewsWithIds.put(R.id.divider3, 20);
        sViewsWithIds.put(R.id.advanceTransferLayout, 21);
        sViewsWithIds.put(R.id.pendingAmtSettled, 22);
        sViewsWithIds.put(R.id.availableSettledAmount, 23);
        sViewsWithIds.put(R.id.advancePaidLayout, 24);
        sViewsWithIds.put(R.id.advancePaid, 25);
        sViewsWithIds.put(R.id.pendingTobeTransferLayout, 26);
        sViewsWithIds.put(R.id.pendingTobeTransfer, 27);
        sViewsWithIds.put(R.id.transferAdvance, 28);
        sViewsWithIds.put(R.id.pendingSettlementLayout, 29);
        sViewsWithIds.put(R.id.pendingAmount, 30);
        sViewsWithIds.put(R.id.viewLastDivider, 31);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public SettlementAmountBreakupLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 32, sIncludes, sViewsWithIds));
    }
    private SettlementAmountBreakupLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.LinearLayout) bindings[3]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.TextView) bindings[25]
            , (android.widget.LinearLayout) bindings[24]
            , (android.widget.LinearLayout) bindings[21]
            , (android.widget.TextView) bindings[23]
            , (android.widget.TextView) bindings[17]
            , (android.widget.LinearLayout) bindings[7]
            , (android.widget.LinearLayout) bindings[2]
            , (android.widget.LinearLayout) bindings[1]
            , (android.view.View) bindings[12]
            , (android.view.View) bindings[20]
            , (android.widget.TextView) bindings[18]
            , (android.widget.TextView) bindings[11]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[30]
            , (android.widget.TextView) bindings[22]
            , (android.widget.LinearLayout) bindings[29]
            , (android.widget.TextView) bindings[27]
            , (android.widget.LinearLayout) bindings[26]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[14]
            , (android.widget.TextView) bindings[16]
            , (android.widget.LinearLayout) bindings[13]
            , (android.widget.LinearLayout) bindings[0]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[6]
            , (android.widget.Button) bindings[28]
            , (android.widget.TextView) bindings[19]
            , (android.view.View) bindings[31]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[15]
            );
        this.topLayout.setTag(null);
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