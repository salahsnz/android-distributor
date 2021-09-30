package com.zopnote.android.merchant.vendor;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.databinding.VendorsActBinding;

import java.util.ArrayList;
import java.util.List;

public class VendorsActivity extends AppCompatActivity {
    private VendorsActBinding binding;
    private VendorViewModel viewmodel;
    private SearchView searchView;
    private VendorsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.vendors_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();

        setupListView();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }



    public static VendorViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        VendorViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(VendorViewModel.class);

        return viewModel;
    }

    private void setupListView() {
        adapter = new VendorsAdapter(this);

        RecyclerView.ItemDecoration dividerDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerDecoration);
        binding.recyclerView.setAdapter(adapter);

        setStatusLoading();

        viewmodel.merchants.observe(this, new Observer<List<Merchant>>() {
            @Override
            public void onChanged(@Nullable List<Merchant> merchants) {
                adapter.setItems(merchants);

                if(merchants.isEmpty()){
                    setStatusEmpty();
                }else{
                    setStatusReady();
                }

               // viewmodel.merchants.removeObserver(this);
            }

        });

    }

    private void setStatusLoading() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.VISIBLE);
    }

    private void setStatusEmpty() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setStatusReady() {
        binding.contentView.setVisibility(View.VISIBLE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        searchView =  (SearchView) menu.findItem(R.id.search).getActionView();
        prepareSearchView(searchView);

        return true;
    }

    private void prepareSearchView(final SearchView searchView) {

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

       /* if (query != null && !query.isEmpty()) {
            searchView.setQuery(query, true);
            searchView.clearFocus();
        }*/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                /*query = queryText;
                if(queryText.isEmpty()){
                    hideAllViews();
                }*/
                ArrayList<Merchant> merchants = new ArrayList<>();
                merchants.clear();
                for(Merchant d : viewmodel.merchants.getValue()){
                    if((d.getOwnerName().contains(queryText) )|| (d.getName().contains(queryText))){

                        merchants.add(d);
                        adapter.setItems(merchants);

                    }

                    //something here
                }
                return false;
            }
        });
        searchView.setIconified(false);
    }
}
