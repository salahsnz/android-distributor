package com.zopnote.android.merchant.intro;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Ravindra on 5/1/2018.
 */

public class ScrollingViewPager extends ViewPager {
    final int DURATION_MS = 500;

    public ScrollingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomScroller();
    }

    private void setCustomScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new CustomScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CustomScroller extends Scroller {
        public CustomScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, DURATION_MS);
        }
    }
}
