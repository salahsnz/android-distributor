package com.zopnote.android.merchant.databinding;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class OrderSummarySelectionBindingImpl extends OrderSummarySelectionBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.radioGroupRoute, 1);
        sViewsWithIds.put(R.id.radioBtnSelectRoutes, 2);
        sViewsWithIds.put(R.id.radioBtnAllRoutes, 3);
        sViewsWithIds.put(R.id.route, 4);
        sViewsWithIds.put(R.id.radioGroupPeriod, 5);
        sViewsWithIds.put(R.id.radioBtnThisMonth, 6);
        sViewsWithIds.put(R.id.radioBtnLastWeek, 7);
        sViewsWithIds.put(R.id.radioBtnThisWeek, 8);
        sViewsWithIds.put(R.id.radioBtnToday, 9);
        sViewsWithIds.put(R.id.endDatePickerLayout, 10);
        sViewsWithIds.put(R.id.startDatePicker, 11);
        sViewsWithIds.put(R.id.endDatePicker, 12);
    }
    // views
    @NonNull
    private final android.widget.LinearLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public OrderSummarySelectionBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 13, sIncludes, sViewsWithIds));
    }
    private OrderSummarySelectionBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.TextView) bindings[12]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.RadioButton) bindings[3]
            , (android.widget.RadioButton) bindings[7]
            , (android.widget.RadioButton) bindings[2]
            , (android.widget.RadioButton) bindings[6]
            , (android.widget.RadioButton) bindings[8]
            , (android.widget.RadioButton) bindings[9]
            , (android.widget.RadioGroup) bindings[5]
            , (android.widget.RadioGroup) bindings[1]
            , (android.widget.Spinner) bindings[4]
            , (android.widget.TextView) bindings[11]
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