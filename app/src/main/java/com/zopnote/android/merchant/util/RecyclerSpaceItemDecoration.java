package com.zopnote.android.merchant.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by nmohideen on 04/02/18.
 */

public class RecyclerSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int spaceInPixels;

    public RecyclerSpaceItemDecoration(Context context, int spaceInDp) {
        float pixelDimension = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                spaceInDp,
                context.getResources().getDisplayMetrics()
        );
        spaceInPixels = Math.round(pixelDimension);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent != null && view != null) {

            int itemPosition = parent.getChildAdapterPosition(view);
            // if needed for additional logic, get itemCount using parent.getAdapter().getItemCount()

            if (itemPosition == 0) {
                outRect.top = spaceInPixels;
            }
            outRect.bottom = spaceInPixels;
        }

    }

}
