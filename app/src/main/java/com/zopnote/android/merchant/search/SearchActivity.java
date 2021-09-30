package com.zopnote.android.merchant.search;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.ViewModelFactory;
import com.zopnote.android.merchant.data.database.CustomerDbHelper;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.databinding.SearchActBinding;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String SEARCH_KEY = "search";
    private   SearchViewModel viewmodel;
    private SearchActBinding binding;
    private SearchCustomerCursorAdaptor cursorAdapter;
    private CustomerDbHelper mDbHelper;
    private SearchView searchView;
    protected String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(SEARCH_KEY);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.search_act);

        setupToolbar();

        viewmodel = obtainViewModel(this);
        viewmodel.init();
        initDb();

        setupView();
    }

    private void initDb() {
        mDbHelper = new CustomerDbHelper(this);
    }

    private void setupView() {

        viewmodel.customersLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loading) {
                updateLoadingView();
            }
        });

        viewmodel.customers.observe(this, new Observer<List<Customer>>() {
            @Override
            public void onChanged(@Nullable List<Customer> customers) {

                viewmodel.customerArrayList = customers;
                viewmodel.customersLoading.setValue(false);

                if(! viewmodel.databaseSynced.getValue().booleanValue()){
                    new DatabaseUpdateAsyncTask().execute();
                }
            }
        });

        hideAllViews();
    }

    private void hideAllViews() {
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.GONE);
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void populateSqliteData(List<Customer> customers) {
        mDbHelper.cleanDatabase();
        mDbHelper.storeCustomers(customers);
    }

    private void updateContentView() {
        if(cursorAdapter != null && cursorAdapter.getCount() > 0){
            binding.contentView.setVisibility(View.VISIBLE);
            binding.emptyView.getRoot().setVisibility(View.GONE);
            return;
        }
        binding.contentView.setVisibility(View.GONE);
        binding.emptyView.getRoot().setVisibility(View.VISIBLE);
    }

    private void updateLoadingView() {
        if (viewmodel.customersLoading.getValue()) {
            binding.loadingView.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        binding.loadingView.getRoot().setVisibility(View.GONE);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
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

        if (query != null && !query.isEmpty()) {
            searchView.setQuery(query, true);
            searchView.clearFocus();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                query = queryText;
                if(queryText.isEmpty()){
                    hideAllViews();
                }
                return false;
            }
        });
        searchView.setIconified(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            searchView.setQuery(query, false);

            Cursor cursor = mDbHelper.getCustomerMatches(query);
            cursorAdapter = new SearchCustomerCursorAdaptor(this, cursor, query);
            binding.listView.setAdapter(cursorAdapter);
            updateContentView();
        }
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

    public static SearchViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        SearchViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(SearchViewModel.class);

        return viewModel;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_KEY, query);
    }

    class DatabaseUpdateAsyncTask extends AsyncTask<Void, Void, Void>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SearchActivity.this);
            progressDialog.setMessage(getString(R.string.preparing_search_message));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            populateSqliteData(viewmodel.customerArrayList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            viewmodel.databaseSynced.setValue(true);

            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }


}
