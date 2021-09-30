package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ViewCustomerFragBindingImpl extends ViewCustomerFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.name, 1);
        sViewsWithIds.put(R.id.nameMobileSeparator, 2);
        sViewsWithIds.put(R.id.mobileNumberLayout, 3);
        sViewsWithIds.put(R.id.mobileNumber, 4);
        sViewsWithIds.put(R.id.addNumber, 5);
        sViewsWithIds.put(R.id.subscriberView, 6);
        sViewsWithIds.put(R.id.doorNumber, 7);
        sViewsWithIds.put(R.id.addressLine1Layout, 8);
        sViewsWithIds.put(R.id.addressLine1, 9);
        sViewsWithIds.put(R.id.addressLine2, 10);
        sViewsWithIds.put(R.id.email, 11);
        sViewsWithIds.put(R.id.route, 12);
        sViewsWithIds.put(R.id.invoiceLayout, 13);
        sViewsWithIds.put(R.id.editInvoice, 14);
        sViewsWithIds.put(R.id.invoiceAmount, 15);
        sViewsWithIds.put(R.id.invoicePaidDateLinear, 16);
        sViewsWithIds.put(R.id.invoicePaidDate, 17);
        sViewsWithIds.put(R.id.cashPaymentReverseButton, 18);
        sViewsWithIds.put(R.id.paymentMode, 19);
        sViewsWithIds.put(R.id.viewBillButton, 20);
        sViewsWithIds.put(R.id.cashPaymentButton, 21);
        sViewsWithIds.put(R.id.sendRemainder, 22);
        sViewsWithIds.put(R.id.callCustomer, 23);
        sViewsWithIds.put(R.id.billHistory, 24);
        sViewsWithIds.put(R.id.addItemButton, 25);
        sViewsWithIds.put(R.id.subscriptionsLayout, 26);
        sViewsWithIds.put(R.id.manageSubscriptionButton, 27);
        sViewsWithIds.put(R.id.subscriptionsContainer, 28);
        sViewsWithIds.put(R.id.notesLayout, 29);
        sViewsWithIds.put(R.id.notes, 30);
        sViewsWithIds.put(R.id.createdLayout, 31);
        sViewsWithIds.put(R.id.created, 32);
    }
    // views
    @NonNull
    private final android.widget.ScrollView mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ViewCustomerFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 33, sIncludes, sViewsWithIds));
    }
    private ViewCustomerFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.Button) bindings[25]
            , (android.widget.ImageView) bindings[5]
            , (android.widget.TextView) bindings[9]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.TextView) bindings[10]
            , (android.widget.Button) bindings[24]
            , (android.widget.LinearLayout) bindings[23]
            , (android.widget.TextView) bindings[21]
            , (android.widget.ImageView) bindings[18]
            , (android.widget.TextView) bindings[32]
            , (android.widget.LinearLayout) bindings[31]
            , (android.widget.TextView) bindings[7]
            , (android.widget.ImageView) bindings[14]
            , (android.widget.TextView) bindings[11]
            , (android.widget.TextView) bindings[15]
            , (android.widget.LinearLayout) bindings[13]
            , (android.widget.TextView) bindings[17]
            , (android.widget.LinearLayout) bindings[16]
            , (android.widget.TextView) bindings[27]
            , (android.widget.TextView) bindings[4]
            , (android.widget.LinearLayout) bindings[3]
            , (android.widget.TextView) bindings[1]
            , (android.widget.TextView) bindings[2]
            , (android.widget.TextView) bindings[30]
            , (android.widget.LinearLayout) bindings[29]
            , (android.widget.TextView) bindings[19]
            , (android.widget.TextView) bindings[12]
            , (android.widget.LinearLayout) bindings[22]
            , (android.widget.ImageView) bindings[6]
            , (android.widget.LinearLayout) bindings[28]
            , (android.widget.LinearLayout) bindings[26]
            , (android.widget.TextView) bindings[20]
            );
        this.mboundView0 = (android.widget.ScrollView) bindings[0];
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