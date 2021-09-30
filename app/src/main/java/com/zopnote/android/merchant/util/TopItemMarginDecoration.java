package com.zopnote.android.merchant.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by nmohideen on 04/02/18.
 */

public class TopItemMarginDecoration extends RecyclerView.ItemDecoration {

    private final int marginInPixels;

    public TopItemMarginDecoration(Context context, int marginInDp) {
        float pixelDimension = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginInDp,
                context.getResources().getDisplayMetrics()
        );
        marginInPixels = Math.round(pixelDimension);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent != null && view != null) {

            int itemPosition = parent.getChildAdapterPosition(view);
            if (itemPosition == 0) {
                outRect.top = marginInPixels;
            }
        }

    }

}
