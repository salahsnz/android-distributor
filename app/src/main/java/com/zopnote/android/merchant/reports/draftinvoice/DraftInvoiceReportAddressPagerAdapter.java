package com.zopnote.android.merchant.reports.draftinvoice;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class DraftInvoiceReportAddressPagerAdapter extends FragmentStatePagerAdapter {
    private List<String> tabs;

    public DraftInvoiceReportAddressPagerAdapter(FragmentManager fm, List<String> tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        String route = tabs.get(position);
        return DraftInvoiceReportFragment.newInstance(route);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    // Support dynamic page addition/removal
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
