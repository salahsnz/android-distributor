package com.zopnote.android.merchant.addcustomer;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.databinding.SubscriptionsFragBinding;
import com.zopnote.android.merchant.util.Validatable;

import java.util.List;

/**
 * Created by nmohideen on 26/12/17.
 */

public class SubscriptionsFragment extends Fragment implements Validatable, SubscriptionsAdapter.ItemClicked {

    private SubscriptionsFragBinding binding;
    private AddCustomerViewModel viewmodel;
    private SubscriptionsAdapter adapter;
    private int previousGridItem  = -1;
    private boolean productsLoaded;

    public SubscriptionsFragment() {
        // Requires empty public constructor
    }

    public static SubscriptionsFragment newInstance() {
        SubscriptionsFragment fragment = new SubscriptionsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SubscriptionsFragBinding.inflate(inflater, container, false);

        binding.recylerView.setHasFixedSize(true);

        adapter = new SubscriptionsAdapter(getActivity(), this);
        RecyclerView.ItemDecoration dividerDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        binding.recylerView.addItemDecoration(dividerDecoration);
        binding.recylerView.setAdapter(adapter);

        // hide all
        binding.contentView.setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewmodel = AddCustomerActivity.obtainViewModel(getActivity());

        // TODO Use an interface
        adapter.setViewModel(viewmodel);
        updateSelectedSummary();

        viewmodel.productsLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loading) {
                updateLoadingView();
            }
        });

        viewmodel.products.observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> products) {
                if( !productsLoaded){
                    viewmodel.productList = products;
                    setupGridView();

                    viewmodel.productsLoading.setValue(false);
                    updateContentView();

                    productsLoaded = true;
                }

            }
        });

        binding.clearAllSelections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewmodel.productIdMap.clear();
                updateSelectedSummary();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupGridView() {
        binding.gridview.setAdapter(new AlphabetGridAdapter(getActivity(), viewmodel.productList));

        adapter.setItems(viewmodel.productList);
        adapter.notifyDataSetChanged();

        binding.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (previousGridItem != -1) {
                    View previousView = binding.gridview.getChildAt(previousGridItem);
                    if(previousView != null){
                        previousView.setBackgroundColor(getResources().getColor(R.color.gray_bg));
                        ((TextView)previousView.findViewById(R.id.label)).setTextColor(getResources().getColor(R.color.text_primary));
                    }
                }
                previousGridItem = position;

                String alphabet = (String) adapterView.getItemAtPosition(position);
                view.setBackgroundColor(getResources().getColor(R.color.green));
                ((TextView)view.findViewById(R.id.label)).setTextColor(getResources().getColor(R.color.white));
                adapter.getFilter().filter(alphabet);
            }
        });
    }

    private void updateContentView() {
        if (adapter.getItemCount() > 0) {
            binding.contentView.setVisibility(View.VISIBLE);
            return;
        }

        binding.contentView.setVisibility(View.GONE);

    }

    private void updateLoadingView() {
        if (viewmodel.productsLoading.getValue()) {
            binding.loadingView.getRoot().setVisibility(View.VISIBLE);
            return;
        }

        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void onClick(Product product) {
        // toggle stored value
        if (viewmodel.productIdMap.containsKey(product.getId())) {
            viewmodel.productIdMap.remove(product.getId());
        } else {
            viewmodel.productIdMap.put(product.getId(), product);
        }

        // update view
        updateSelectedSummary();
    }

    private void updateSelectedSummary() {
        if (viewmodel.productIdMap.size() > 0) {
            binding.selectedSummaryHeading.setText(getActivity().getResources().getString(R.string.selected_heading, viewmodel.productIdMap.size()));
            binding.selectedSummaryMessage.setText(getSelectedSummaryMessage());
            binding.selectedSummaryLayout.setVisibility(View.VISIBLE);
        } else {
            binding.selectedSummaryLayout.setVisibility(View.GONE);
        }
    }

    private String getSelectedSummaryMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : viewmodel.productIdMap.keySet()) {
            if (stringBuilder.length() > 0) {
                // add separator
                stringBuilder.append(", ");
            }
            stringBuilder.append(viewmodel.productIdMap.get(key).getName());
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean validate() {
        if (viewmodel.productIdMap.size() > 0) {
            return true;
        } else {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.subscription_error_message), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
