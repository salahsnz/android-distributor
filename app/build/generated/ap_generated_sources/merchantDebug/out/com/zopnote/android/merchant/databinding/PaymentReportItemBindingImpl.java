package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class PaymentReportItemBindingImpl extends PaymentReportItemBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.route, 1);
        sViewsWithIds.put(R.id.billedLayout, 2);
        sViewsWithIds.put(R.id.totalNumberBilled, 3);
        sViewsWithIds.put(R.id.totalAmountBilled, 4);
        sViewsWithIds.put(R.id.paidOnlineLayout, 5);
        sViewsWithIds.put(R.id.totalNumberPaidOnlinePercentage, 6);
        sViewsWithIds.put(R.id.totalNumberPaidOnline, 7);
        sViewsWithIds.put(R.id.totalAmountPaidOnline, 8);
        sViewsWithIds.put(R.id.paidCashLayout, 9);
        sViewsWithIds.put(R.id.totalNumberPaidCashPercentage, 10);
        sViewsWithIds.put(R.id.totalNumberPaidCash, 11);
        sViewsWithIds.put(R.id.totalAmountPaidCash, 12);
        sViewsWithIds.put(R.id.cashDiv, 13);
        sViewsWithIds.put(R.id.paidChequeLayout, 14);
        sViewsWithIds.put(R.id.totalNumberPaidChequePercentage, 15);
        sViewsWithIds.put(R.id.totalNumberPaidCheque, 16);
        sViewsWithIds.put(R.id.totalAmountPaidCheque, 17);
        sViewsWithIds.put(R.id.chequeDiv, 18);
        sViewsWithIds.put(R.id.paidGPayLayout, 19);
        sViewsWithIds.put(R.id.totalNumberPaidGPayPercentage, 20);
        sViewsWithIds.put(R.id.totalNumberPaidGPay, 21);
        sViewsWithIds.put(R.id.totalAmountPaidGpay, 22);
        sViewsWithIds.put(R.id.GpayDiv, 23);
        sViewsWithIds.put(R.id.paidPaytmLayout, 24);
        sViewsWithIds.put(R.id.totalNumberPaidPaytmPercentage, 25);
        sViewsWithIds.put(R.id.totalNumberPaidPaytm, 26);
        sViewsWithIds.put(R.id.totalAmountPaidPaytm, 27);
        sViewsWithIds.put(R.id.paytmDiv, 28);
        sViewsWithIds.put(R.id.paidPhonepeLayout, 29);
        sViewsWithIds.put(R.id.totalNumberPaidPhonepePercentage, 30);
        sViewsWithIds.put(R.id.totalNumberPaidPhonepe, 31);
        sViewsWithIds.put(R.id.totalAmountPaidPhonepe, 32);
        sViewsWithIds.put(R.id.phonepeDiv, 33);
        sViewsWithIds.put(R.id.paidUPILayout, 34);
        sViewsWithIds.put(R.id.totalNumberPaidUPIPercentage, 35);
        sViewsWithIds.put(R.id.totalNumberPaidUPI, 36);
        sViewsWithIds.put(R.id.totalAmountPaidUPI, 37);
        sViewsWithIds.put(R.id.upiDiv, 38);
        sViewsWithIds.put(R.id.paidOtherLayout, 39);
        sViewsWithIds.put(R.id.totalNumberPaidOtherPercentage, 40);
        sViewsWithIds.put(R.id.totalNumberPaidOther, 41);
        sViewsWithIds.put(R.id.totalAmountPaidOther, 42);
        sViewsWithIds.put(R.id.otherDiv, 43);
        sViewsWithIds.put(R.id.pendingLayout, 44);
        sViewsWithIds.put(R.id.totalNumberUnpaidPercentage, 45);
        sViewsWithIds.put(R.id.totalNumberUnpaid, 46);
        sViewsWithIds.put(R.id.totalAmountUnpaid, 47);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public PaymentReportItemBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 48, sIncludes, sViewsWithIds));
    }
    private PaymentReportItemBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.view.View) bindings[23]
            , (android.widget.LinearLayout) bindings[2]
            , (android.view.View) bindings[13]
            , (android.view.View) bindings[18]
            , (android.view.View) bindings[43]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.LinearLayout) bindings[14]
            , (android.widget.LinearLayout) bindings[19]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.LinearLayout) bindings[39]
            , (android.widget.LinearLayout) bindings[24]
            , (android.widget.LinearLayout) bindings[29]
            , (android.widget.LinearLayout) bindings[34]
            , (android.view.View) bindings[28]
            , (android.widget.LinearLayout) bindings[44]
            , (android.view.View) bindings[33]
            , (android.widget.TextView) bindings[1]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[17]
            , (android.widget.TextView) bindings[22]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[42]
            , (android.widget.TextView) bindings[27]
            , (android.widget.TextView) bindings[32]
            , (android.widget.TextView) bindings[37]
            , (android.widget.TextView) bindings[47]
            , (android.widget.TextView) bindings[3]
            , (android.widget.TextView) bindings[11]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[16]
            , (android.widget.TextView) bindings[15]
            , (android.widget.TextView) bindings[21]
            , (android.widget.TextView) bindings[20]
            , (android.widget.TextView) bindings[7]
            , (android.widget.TextView) bindings[6]
            , (android.widget.TextView) bindings[41]
            , (android.widget.TextView) bindings[40]
            , (android.widget.TextView) bindings[26]
            , (android.widget.TextView) bindings[25]
            , (android.widget.TextView) bindings[31]
            , (android.widget.TextView) bindings[30]
            , (android.widget.TextView) bindings[36]
            , (android.widget.TextView) bindings[35]
            , (android.widget.TextView) bindings[46]
            , (android.widget.TextView) bindings[45]
            , (android.view.View) bindings[38]
            );
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
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