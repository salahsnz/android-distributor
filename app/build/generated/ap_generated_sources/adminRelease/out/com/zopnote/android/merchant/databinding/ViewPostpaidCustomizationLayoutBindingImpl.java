package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ViewPostpaidCustomizationLayoutBindingImpl extends ViewPostpaidCustomizationLayoutBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = new android.databinding.ViewDataBinding.IncludedLayouts(7);
        sIncludes.setIncludes(0, 
            new String[] {"view_delivery_days_and_pricing_layout"},
            new int[] {1},
            new int[] {com.zopnote.android.merchant.R.layout.view_delivery_days_and_pricing_layout});
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.type, 2);
        sViewsWithIds.put(R.id.paymentMode, 3);
        sViewsWithIds.put(R.id.pricingType, 4);
        sViewsWithIds.put(R.id.deliveryChargeLayout, 5);
        sViewsWithIds.put(R.id.deliveryCharge, 6);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ViewPostpaidCustomizationLayoutBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 7, sIncludes, sViewsWithIds));
    }
    private ViewPostpaidCustomizationLayoutBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.TextView) bindings[6]
            , (android.widget.LinearLayout) bindings[5]
            , (com.zopnote.android.merchant.databinding.ViewDeliveryDaysAndPricingLayoutBinding) bindings[1]
            , (android.widget.TextView) bindings[3]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[2]
            );
        setContainedBinding(this.deliveryDaysAndPricingLayout);
        this.mboundView0 = (android.widget.LinearLayout) bindings[0];
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
        deliveryDaysAndPricingLayout.invalidateAll();
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        if (deliveryDaysAndPricingLayout.hasPendingBindings()) {
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
        deliveryDaysAndPricingLayout.setLifecycleOwner(lifecycleOwner);
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeDeliveryDaysAndPricingLayout((com.zopnote.android.merchant.databinding.ViewDeliveryDaysAndPricingLayoutBinding) object, fieldId);
        }
        return false;
    }
    private boolean onChangeDeliveryDaysAndPricingLayout(com.zopnote.android.merchant.databinding.ViewDeliveryDaysAndPricingLayoutBinding DeliveryDaysAndPricingLayout, int fieldId) {
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
        executeBindingsOn(deliveryDaysAndPricingLayout);
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): deliveryDaysAndPricingLayout
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}