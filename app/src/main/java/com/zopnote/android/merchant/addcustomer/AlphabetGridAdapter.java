package com.zopnote.android.merchant.addcustomer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class AlphabetGridAdapter extends BaseAdapter {

    private Context context;

    private List<String> labels;

    public AlphabetGridAdapter(Context context, List<Product> productList) {
        this.context = context;

        labels = getLabels(productList);
    }

    private List<String> getLabels(List<Product> productList) {
        List<String> labelsList = new ArrayList<>();

        for (Product product: productList) {
            String productInitial = product.getName().substring(0,1);
            if( ! labelsList.contains(productInitial)){
                labelsList.add(productInitial);
            }
        }
        labelsList.add(context.getResources().getString(R.string.products_grid_item_all_label));
        return labelsList;
    }

    @Override
    public int getCount() {
        return labels.size();
    }

    @Override
    public Object getItem(int i) {
        return labels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.alphabet_grid_item, null);
        } else {
            view = convertView;
        }

        ((TextView) view.findViewById(R.id.label)).setText(labels.get(i));

        return view;
    }
}
