package com.zopnote.android.merchant.util;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by nmohideen on 30/01/18.
 */

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Context context;
    private List<T> items;

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setItems(final List<T> newItems) {
        if (this.items == null) {
            this.items = newItems;
            notifyItemRangeInserted(0, newItems.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return BaseRecyclerAdapter.this.items.size();
                }

                @Override
                public int getNewListSize() {
                    return newItems.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    // use our abstract method
                    return areItemsEqual(oldItemPosition, newItemPosition, newItems);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    // use our abstract method
                    return areContentsSame(oldItemPosition, newItemPosition, newItems);
                }
            });
            this.items = newItems;
            result.dispatchUpdatesTo(this);
        }
    }

    public abstract boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<T> newItems);

    public abstract boolean areContentsSame(int oldItemPosition, int newItemPosition, List<T> newItems);

    public Context getContext() {
        return context;
    }

    protected List<T> getItems() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
