package com.zopnote.android.merchant.products;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.databinding.ProductItemBinding;
import com.zopnote.android.merchant.products.editproduct.EditProductActivity;
import com.zopnote.android.merchant.util.BaseRecyclerAdapter;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.ObjectsUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProductsAdapter extends BaseRecyclerAdapter<Product, ProductsAdapter.ProductViewHolder> {

    private ImageLoader imageLoader;
    private List<String> daysList;

    public ProductsAdapter(Context context) {
        super(context);
        imageLoader = VolleyManager.getInstance(context).getImageLoader();
        initializeDayOfWeekArray();
    }

    private void initializeDayOfWeekArray() {
        daysList = new ArrayList<>();
        daysList.add("Monday");
        daysList.add("Tuesday");
        daysList.add("Wednesday");
        daysList.add("Thursday");
        daysList.add("Friday");
        daysList.add("Saturday");
        daysList.add("Sunday");
    }

    @Override
    public boolean areItemsEqual(int oldItemPosition, int newItemPosition, List<Product> newItems) {
        Product oldItem = getItems().get(oldItemPosition);
        Product newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsSame(int oldItemPosition, int newItemPosition, List<Product> newItems) {
        Product oldItem = getItems().get(oldItemPosition);
        Product newItem = newItems.get(newItemPosition);
        return ObjectsUtil.equals(newItem.getId(), oldItem.getId())
                && (ObjectsUtil.equals(newItem.getPrice(), oldItem.getPrice()))
                && (ObjectsUtil.equals(newItem.getServiceCharge(), oldItem.getServiceCharge()));
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.product_item,
                        parent, false);
        final ProductViewHolder vh = new ProductViewHolder(binding);
        vh.itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = vh.getAdapterPosition();

            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final Product product = getItems().get(position);

        holder.itemBinding.title.setText(product.getName()+" ("+product.getShortCode()+")");
        holder.itemBinding.language.setText(product.getLanguage());

        String frequency = product.getFrequency();
        String capitalizedFrequency = frequency.substring(0,1).toUpperCase() + frequency.substring(1).toLowerCase();
        holder.itemBinding.frequency.setText(capitalizedFrequency);

        String pricingMode = product.getPricingMode();

        if(pricingMode.equalsIgnoreCase("DAILY")){

            String price = getPriceSummary(product.getPrice());
            holder.itemBinding.price.setText(price);

            holder.itemBinding.daysText.setText(getContext().getResources().getString(R.string.subscription_price_per_issue_label));

            if (isMultiPrice(product.getPrice())) {
                holder.itemBinding.showAllPrices.setVisibility(View.VISIBLE);
                setUpShowAllPrices(holder.itemBinding.showAllPrices, product);
            } else {
                holder.itemBinding.showAllPrices.setVisibility(View.GONE);
            }
        }else if (pricingMode.equalsIgnoreCase("ONDEMAND")) {
            holder.itemBinding.serviceChargeLayout.setVisibility(View.GONE);
            holder.itemBinding.llPrice.setVisibility(View.GONE);
            holder.itemBinding.llPrice2.setVisibility(View.GONE);
            holder.itemBinding.showAllPrices.setVisibility(View.GONE);
        }else{

            Map<String, Double> daysPriceMap = product.getPrice();

            Map.Entry<String,Double> entry = daysPriceMap.entrySet().iterator().next();
            String dayText;

            if(entry.getKey().equalsIgnoreCase("issue")){
                dayText = getContext().getResources().getString(R.string.subscription_price_per_issue_label);
            }else{
               dayText = entry.getKey();
            }

            holder.itemBinding.daysText.setText(dayText);

            String price = FormatUtil.AMOUNT_FORMAT.format(entry.getValue());
            holder.itemBinding.price.setText(price);

            holder.itemBinding.showAllPrices.setVisibility(View.GONE);
        }

        if (product.getServiceCharge() != null && product.getServiceCharge() > 0d) {
            String serviceCharge = FormatUtil.AMOUNT_FORMAT.format(product.getServiceCharge());
            holder.itemBinding.serviceCharge.setText(serviceCharge);
            holder.itemBinding.serviceChargeLayout.setVisibility(View.VISIBLE);
        } else {
            holder.itemBinding.serviceChargeLayout.setVisibility(View.GONE);
        }

        holder.itemBinding.logo.setDefaultImageResId(R.drawable.ic_image_black_24dp);
        holder.itemBinding.logo.setErrorImageResId(R.drawable.ic_broken_image_black_24dp);
        holder.itemBinding.logo.setImageUrl(product.getLogoUrl(), imageLoader);

        holder.itemBinding.productActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductOptions(v, product);
            }
        });
    }

    private void showProductOptions(View view, final Product product) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);

        popupMenu.getMenu().add(R.string.edit_product_price_menu_label).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), EditProductActivity.class);
                intent.putExtra(Extras.PRODUCT, product);
                intent.putExtra(Extras.ADD_PRODUCT, false);
                getContext().startActivity(intent);
                return true;
            }
        });
        popupMenu.show();
    }



    private void setUpShowAllPrices(TextView showAllPrices, final Product product) {
        showAllPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog. Builder builder = new AlertDialog.Builder(getContext());

                LayoutInflater inflater = LayoutInflater.from(getContext());

                View view = inflater.inflate(R.layout.subscription_price_detail, null);

                if(product.getServiceCharge() != null && product.getServiceCharge() > 0d){
                    view.findViewById(R.id.serviceChargeLayout).setVisibility(View.VISIBLE);

                    TextView serviceChargeTextView = view.findViewById(R.id.serviceCharge);
                    String serviceCharge = FormatUtil.AMOUNT_FORMAT.format(product.getServiceCharge());
                    serviceChargeTextView.setText(serviceCharge);

                }else {
                    view.findViewById(R.id.serviceChargeLayout).setVisibility(View.GONE);
                }

                final ListView listview = view.findViewById(R.id.listView);
                final ArrayAdapter<String> arrayAdapter = new PriceListAdapter(getContext(), R.layout.subscription_price_list_item,
                        getSortedMondayToSundayPrice(product.getPrice()), daysList);
                listview.setAdapter(arrayAdapter);
                listview.setDividerHeight(0);
                listview.setDivider(null);

                builder.setView(view);

                builder.setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private String getPriceSummary(Map<String, Double> prices) {
        StringBuilder stringBuilder = new StringBuilder();

        Map<Integer, Double> sortedMondayToSundayPrice = getSortedMondayToSundayPrice(prices);
        Double previousPrice = null;
        for (Integer dayOfWeek : sortedMondayToSundayPrice.keySet()) {
            Double currentPrice = sortedMondayToSundayPrice.get(dayOfWeek);
            if (previousPrice == null || currentPrice.compareTo(previousPrice) != 0) {
                if (currentPrice.compareTo(0d) != 0) {
                    if (stringBuilder.length() > 0) {
                        // add separator
                        stringBuilder.append(" / ");
                    }
                    stringBuilder.append(FormatUtil.AMOUNT_FORMAT.format(currentPrice));
                }
            }
            previousPrice = currentPrice;
        }

        return stringBuilder.toString();
    }

    private boolean isMultiPrice(Map<String, Double> prices) {
        Map<Integer, Double> sortedMondayToSundayPrice = getSortedMondayToSundayPrice(prices);
        Double previousPrice = null;
        for (Integer dayOfWeek : sortedMondayToSundayPrice.keySet()) {
            Double currentPrice = sortedMondayToSundayPrice.get(dayOfWeek);
            if (previousPrice != null && currentPrice.compareTo(previousPrice) != 0) {
                return true;
            }
            previousPrice = currentPrice;
        }
        return false;
    }

    private Map<Integer, Double> getSortedMondayToSundayPrice(Map<String, Double> prices) {
        Map<Integer, Double> mondayToSundaySortedMap = new TreeMap<>();
        for (Map.Entry<String, Double> entry : prices.entrySet()) {
            String dayOfWeek = entry.getKey();
            Double price = entry.getValue();

            int position = daysList.indexOf(dayOfWeek);
            if(position >= 0){
                mondayToSundaySortedMap.put(position, price);
            }
        }
        return mondayToSundaySortedMap;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        final ProductItemBinding itemBinding;

        public ProductViewHolder(ProductItemBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
    }
}
