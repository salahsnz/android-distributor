package com.zopnote.android.merchant.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.R;

/**
 * Created by nmohideen on 23/06/18.
 */

public class HomeGridAdapter extends BaseAdapter {

    private Context context;

    private String[] labels;
    private int[] icons;

    public HomeGridAdapter(Context context,boolean isOndemand ) {
        this.context = context;

        if(BuildConfig.PRODUCT_FLAVOUR_MERCHANT == false) {
            labels = new String[]{
                    context.getResources().getString(R.string.nav_customers),
                    context.getResources().getString(R.string.nav_products),
                    context.getResources().getString(R.string.nav_indent),
                    context.getResources().getString(R.string.nav_reports),
                    context.getResources().getString(R.string.nav_sent_remainder),
                    context.getResources().getString(R.string.nav_admin_panel)

            };
            icons = new int[]{
                    R.drawable.ic_person_black_24dp,
                    R.drawable.ic_subject_black_24dp,
                    R.drawable.ic_assignment_black_24dp,
                    R.drawable.ic_assessment_black_24dp,
                    R.drawable.ic_sms_black_24dp,
                    R.drawable.ic_update_black_24dp
            };
        }else{
            if (isOndemand) {
                labels = new String[]{
                        context.getResources().getString(R.string.add_order),
                        context.getResources().getString(R.string.nav_customers),
                        context.getResources().getString(R.string.items_label),
                        //    context.getResources().getString(R.string.nav_indent),
                        context.getResources().getString(R.string.nav_reports),

                };

                icons = new int[]{
                        R.drawable.ic_add_shopping_cart_black_24dp,
                        R.drawable.ic_person_black_24dp,
                        R.drawable.ic_subject_black_24dp,
                        // R.drawable.ic_assignment_black_24dp,
                        R.drawable.ic_assessment_black_24dp
                };
            }else {
                labels = new String[]{
                        context.getResources().getString(R.string.nav_customers),
                        context.getResources().getString(R.string.nav_products),
                        context.getResources().getString(R.string.nav_indent),
                        context.getResources().getString(R.string.nav_reports),

                };

                icons = new int[]{
                        R.drawable.ic_person_black_24dp,
                        R.drawable.ic_subject_black_24dp,
                        R.drawable.ic_assignment_black_24dp,
                        R.drawable.ic_assessment_black_24dp
                };
            }
        }
    }

    @Override
    public int getCount() {
        return labels.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.home_grid_item, null);
        } else {
            view = convertView;
        }

        ((ImageView) view.findViewById(R.id.icon)).setImageResource(icons[i]);
        ((TextView) view.findViewById(R.id.label)).setText(labels[i]);

        return view;
    }

}
