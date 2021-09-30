package com.zopnote.android.merchant.products;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.util.FormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PriceListAdapter extends ArrayAdapter {

    private final Context context;
    private final List<String> daysList;
    private Map<Integer, Double> priceMap;
    private List<Integer> daysIndex;
    private List<Double> pricesList;

    static class ViewHolder {
        public TextView price;
        public TextView dayOfWeek;
    }

    public PriceListAdapter(@NonNull Context context, int resource, Map<Integer, Double> priceMap, List<String> daysList) {
        super(context, resource);
        this.context = context;

        this.priceMap = priceMap;
        this.daysList = daysList;

        this.daysIndex = new ArrayList<>(priceMap.keySet());
        this.pricesList = new ArrayList<>(priceMap.values());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.subscription_price_list_item, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.price = rowView.findViewById(R.id.price);
            viewHolder.dayOfWeek =  rowView.findViewById(R.id.dayOfWeek);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        int dayOfTheWeekIndex = daysIndex.get(position);
        Double price = pricesList.get(position);
        String day = daysList.get(dayOfTheWeekIndex);

        if (price.compareTo(0d) != 0) {
            String rupeePrefixedAmount = FormatUtil.getRupeePrefixedAmount(context, price, FormatUtil.AMOUNT_FORMAT);
            holder.price.setText(rupeePrefixedAmount);
            holder.dayOfWeek.setText(day);
        } else {
            holder.price.setText(context.getResources().getString(R.string.day_of_week_no_publication_label));
            holder.dayOfWeek.setText(day);
        }

        return rowView;
    }

    @Override
    public int getCount() {
        return priceMap.size();
    }
}
