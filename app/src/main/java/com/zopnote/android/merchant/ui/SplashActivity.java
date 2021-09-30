package com.zopnote.android.merchant.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zopnote.android.merchant.util.AppLaunchUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppLaunchUtil.startNextActivity(this);

        finish();
    }
}
