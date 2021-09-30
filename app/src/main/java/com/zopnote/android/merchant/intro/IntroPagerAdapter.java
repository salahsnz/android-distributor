package com.zopnote.android.merchant.intro;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.databinding.IntroPageBinding;

import java.util.List;

/**
 * Created by Ravindra on 5/1/2018.
 */

public class IntroPagerAdapter extends PagerAdapter {

    private Context context;
    List<IntroActivity.IntroPageInfo> introPageInfos;

    public IntroPagerAdapter(Context context, List<IntroActivity.IntroPageInfo> introPageInfos) {
        this.context = context;
        this.introPageInfos = introPageInfos;
    }

    @Override
    public int getCount() {
        return introPageInfos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        IntroPageBinding binding = DataBindingUtil.inflate(inflater, R.layout.intro_page, container, false);

        IntroActivity.IntroPageInfo introPageInfo = introPageInfos.get(position);

        binding.title.setText(introPageInfo.title);
        binding.desc.setText(introPageInfo.desc);
        binding.image.setImageResource(introPageInfo.imageResId);

        View view = binding.getRoot();

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
