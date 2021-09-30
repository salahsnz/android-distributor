package com.zopnote.android.merchant.intro;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.ScreenName;
import com.zopnote.android.merchant.util.AppLaunchUtil;
import com.zopnote.android.merchant.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class IntroActivity extends AppCompatActivity {

    private static final long AUTO_SCROLL_START_DELAY_MILLIS = 500;
    private static final long AUTO_SCROLL_SHOW_DURATION_MILLIS = 5000;


    private ScrollingViewPager viewPager;
    private int currentPage = 0;
    private Timer timer;
    private boolean isAutoScrollRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_act);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setupButton();

        setupAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAnalytics.getInstance(this)
                .setCurrentScreen(this, ScreenName.INTRO, "IntroActivity");
    }

    private void setupButton() {
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! Utils.enforceConnection(IntroActivity.this)) {
                    return;
                }

                if (! Utils.enforceGooglePlayServices(IntroActivity.this)) {
                    return;
                }

                AppLaunchUtil.setSessionSkipIntro(true);
                AppLaunchUtil.startNextActivity(IntroActivity.this);

                Analytics.logEvent(Event.BUTTON_INTRO_GET_STARTED);

                finish();
            }
        });
    }

    private void setupAdapter() {
        List<IntroPageInfo> introPages = getIntroPages();
        IntroPagerAdapter introPagerAdapter = new IntroPagerAdapter(this, introPages);

        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(introPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(timer != null && isAutoScrollRunning){
                    timer.cancel();
                    isAutoScrollRunning = false;
                    Analytics.logEvent(Event.NAV_INTRO_BANNER);
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if( ! isAutoScrollRunning){
                    String introPage = String.format(ScreenName.INTRO + " - %d", position + 1);
                    FirebaseAnalytics.getInstance(IntroActivity.this)
                            .setCurrentScreen(IntroActivity.this, introPage, "IntroActivity" );
                }
            }
        });

        startAutoScroll(introPages.size());
    }

    private List<IntroPageInfo> getIntroPages() {
        List<IntroPageInfo> introPages = new ArrayList<>(1);

        IntroPageInfo pageInfo = new IntroPageInfo(
                getResources().getString(R.string.intro_page1_title),
                getResources().getString(R.string.intro_page1_desc),
                R.drawable.intro_home
        );
        introPages.add(pageInfo);
        return introPages;
    }

    private void startAutoScroll(final int size) {
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == size) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                isAutoScrollRunning = true;
                handler.post(update);
            }
        }, AUTO_SCROLL_START_DELAY_MILLIS, AUTO_SCROLL_SHOW_DURATION_MILLIS);
    }

    public class IntroPageInfo {
        public final String title;
        public final String desc;
        public final int imageResId;

        public IntroPageInfo(String title, String desc, int imageResId) {
            this.title = title;
            this.desc = desc;
            this.imageResId = imageResId;
        }
    }
}
