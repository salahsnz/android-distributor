package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class EditProductFragBindingImpl extends EditProductFragBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.name, 1);
        sViewsWithIds.put(R.id.paperPricingModeSelectionLayout, 2);
        sViewsWithIds.put(R.id.radioGroupPricingMode, 3);
        sViewsWithIds.put(R.id.radioBtnPaperDailyPrice, 4);
        sViewsWithIds.put(R.id.radioBtnPaperMonthlyPrice, 5);
        sViewsWithIds.put(R.id.magazinePricingModeSelectionLayout, 6);
        sViewsWithIds.put(R.id.perIssuePrice, 7);
        sViewsWithIds.put(R.id.publicationDaysAndPricingLayout, 8);
        sViewsWithIds.put(R.id.publicationDaysAndPriceLabel, 9);
        sViewsWithIds.put(R.id.publicationDaysAndPriceContainer, 10);
        sViewsWithIds.put(R.id.magazineIsPromotedSelectionLayout, 11);
        sViewsWithIds.put(R.id.radioGroupIsPromoted, 12);
        sViewsWithIds.put(R.id.radioBtnPromoted, 13);
        sViewsWithIds.put(R.id.radioBtnNotPromoted, 14);
        sViewsWithIds.put(R.id.deliveryChargeLayout, 15);
        sViewsWithIds.put(R.id.radioGroupDeliveryCharge, 16);
        sViewsWithIds.put(R.id.radioBtnAddDeliveryCharge, 17);
        sViewsWithIds.put(R.id.radioBtnNoDeliveryCharge, 18);
        sViewsWithIds.put(R.id.deliveryChargeAmountLayout, 19);
        sViewsWithIds.put(R.id.deliveryCharge, 20);
        sViewsWithIds.put(R.id.saveProductChangesButton, 21);
    }
    // views
    @NonNull
    private final android.widget.ScrollView mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public EditProductFragBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 22, sIncludes, sViewsWithIds));
    }
    private EditProductFragBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.EditText) bindings[20]
            , (android.widget.LinearLayout) bindings[19]
            , (android.widget.LinearLayout) bindings[15]
            , (android.widget.LinearLayout) bindings[11]
            , (android.widget.LinearLayout) bindings[6]
            , (android.widget.TextView) bindings[1]
            , (android.widget.LinearLayout) bindings[2]
            , (android.widget.EditText) bindings[7]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.TextView) bindings[9]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.RadioButton) bindings[17]
            , (android.widget.RadioButton) bindings[18]
            , (android.widget.RadioButton) bindings[14]
            , (android.widget.RadioButton) bindings[4]
            , (android.widget.RadioButton) bindings[5]
            , (android.widget.RadioButton) bindings[13]
            , (android.widget.RadioGroup) bindings[16]
            , (android.widget.RadioGroup) bindings[12]
            , (android.widget.RadioGroup) bindings[3]
            , (android.widget.Button) bindings[21]
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