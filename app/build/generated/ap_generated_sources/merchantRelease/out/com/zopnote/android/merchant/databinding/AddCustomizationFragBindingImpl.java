package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class AddCustomizationFragBindingImpl extends AddCustomizationFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.name, 1);
        sViewsWithIds.put(R.id.radioGroupPaymentMode, 2);
        sViewsWithIds.put(R.id.radioBtnAnnualSubscription, 3);
        sViewsWithIds.put(R.id.radioBtnRegularSubscription, 4);
        sViewsWithIds.put(R.id.annualSubscriptionDateSelectLayout, 5);
        sViewsWithIds.put(R.id.startDatePickerLayout, 6);
        sViewsWithIds.put(R.id.startDatePicker, 7);
        sViewsWithIds.put(R.id.endDatePickerLayout, 8);
        sViewsWithIds.put(R.id.endDatePicker, 9);
        sViewsWithIds.put(R.id.paperPricingModeSelectionLayout, 10);
        sViewsWithIds.put(R.id.radioGroupPricingMode, 11);
        sViewsWithIds.put(R.id.radioBtnPaperDailyPrice, 12);
        sViewsWithIds.put(R.id.radioBtnPaperMonthlyPrice, 13);
        sViewsWithIds.put(R.id.magazinePricingModeSelectionLayout, 14);
        sViewsWithIds.put(R.id.perIssuePrice, 15);
        sViewsWithIds.put(R.id.deliveryDaysAndPricingLayout, 16);
        sViewsWithIds.put(R.id.deliveryDaysAndPriceContainer, 17);
        sViewsWithIds.put(R.id.deliveryChargeLayout, 18);
        sViewsWithIds.put(R.id.radioGroupDeliveryCharge, 19);
        sViewsWithIds.put(R.id.radioBtnAddDeliveryCharge, 20);
        sViewsWithIds.put(R.id.radioBtnNoDeliveryCharge, 21);
        sViewsWithIds.put(R.id.deliveryChargeAmountLayout, 22);
        sViewsWithIds.put(R.id.deliveryCharge, 23);
        sViewsWithIds.put(R.id.saveSubscriptionChangesButton, 24);
    }
    // views
    @NonNull
    private final android.widget.ScrollView mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public AddCustomizationFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 25, sIncludes, sViewsWithIds));
    }
    private AddCustomizationFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.EditText) bindings[23]
            , (android.widget.LinearLayout) bindings[22]
            , (android.widget.LinearLayout) bindings[18]
            , (android.widget.LinearLayout) bindings[17]
            , (android.widget.LinearLayout) bindings[16]
            , (android.widget.TextView) bindings[9]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.LinearLayout) bindings[14]
            , (android.widget.TextView) bindings[1]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.EditText) bindings[15]
            , (android.widget.RadioButton) bindings[20]
            , (android.widget.RadioButton) bindings[3]
            , (android.widget.RadioButton) bindings[21]
            , (android.widget.RadioButton) bindings[12]
            , (android.widget.RadioButton) bindings[13]
            , (android.widget.RadioButton) bindings[4]
            , (android.widget.RadioGroup) bindings[19]
            , (android.widget.RadioGroup) bindings[2]
            , (android.widget.RadioGroup) bindings[11]
            , (android.widget.Button) bindings[24]
            , (android.widget.TextView) bindings[7]
            , (android.widget.LinearLayout) bindings[6]
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